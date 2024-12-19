from msal import ConfidentialClientApplication
import json
import logging
import requests
import time
from typing import List, Union


class MsRequestsWrapper:
    def __init__(self, config_file: str):
        """
        Constructor method for the MsRequestsWrapper class. This class is used to make requests to the Morgan Stanley API.

        Parameters
        ----------
        config_file: str
            The path to the config file to load.
        """
        self.config = self.load_config(config_file)
        self.url = self.config["url"]
        self.proxies = self.get_proxies(self.config)
        self.requests_ca_bundle = self.get_requests_ca_bundle(self.config)
        self.app = self.get_client_app(self.config)

    def load_config(self, config_file: str):
        """
        Load the config map from a JSON file with the given path.

        Parameters
        ----------
        config_file: str
            The path to the config file to load.
        """
        with open(config_file, mode="r") as f:
            return json.load(f)

    def load_private_key(self, private_key_file: str):
        """
        Load the private key from a PEM file with the given path.

        Parameters
        ----------
        private_key_file: str
            The path to the private key to load.
        """
        with open(private_key_file, mode="r") as f:
            return f.read()

    def get_proxies(self, config: dict) -> Union[dict, None]:
        """
        Returns proxy config from the config dictionary if the correct config has been provided.
        Otherwise returns None.

        Parameters
        ----------
        config: dict
            The config map to use.
        """
        proxy_host = config.get("proxy_host")
        proxy_port = config.get("proxy_port")
        proxies = None
        if proxy_host is not None:
            if proxy_port is None:
                raise Exception("Missing proxy port.")
            proxies = {
                "http": f"{proxy_host}:{proxy_port}",
                "https": f"{proxy_host}:{proxy_port}",
            }
        return proxies

    def get_requests_ca_bundle(self, config: dict) -> Union[str, bool]:
        """
        Get the system CA bundle, if it's set. This is only necessary if your environment uses a proxy, since the bundled certificates will not work.
        This returns True if no CA bundle is set; this tells requests to use the default, bundled certificates.

        Parameters
        ----------
        config: dict
            The config map to use.

        Returns
        -------
        If SSL has been explicitly disabled: False
        If SSL is enabled and should use the default settings: False
        If a custom SSL bundle will be used: a string with an absolute path to a .pem file on the system. The config map to use.
        """

        if config.get("disable_ssl_verification"):
            return False
        return config.get("requests_ca_bundle") or True

    def get_client_app(self, config: dict):
        """
        Configures an MSAL client application, that can later be used to request an access token.

        Parameters
        ----------
        config: dict
            The config map to use.
        """
        client_id = config["client_id"]
        thumbprint = config["thumbprint"]
        private_key_path = config["private_key_file"]
        authority = f"https://login.microsoftonline.com/{config['tenant']}"
        proxies = self.get_proxies(config)

        private_key = self.load_private_key(private_key_path)

        requests_ca_bundle = self.get_requests_ca_bundle(config)

        return ConfidentialClientApplication(
            client_id=client_id,
            authority=authority,
            client_credential={"thumbprint": thumbprint, "private_key": private_key},
            proxies=proxies,
            verify=requests_ca_bundle,
        )

    def acquire_token(self, app: ConfidentialClientApplication, scopes: List[str]):
        """
        Gets an access token against the provided scopes using a pre-configured MSAL app.

        Parameters
        ----------
        app: ConfidentialClientApplication
            The preconfigured MSAL ConfidentialClientApplication to request a token with.
        scopes: List[str]
            The list of scopes to request a token against.
        """

        result = app.acquire_token_silent(scopes, account=None)

        if not result:
            print(
                "No suitable token exists in cache. Retrieving a new token from Azure AD."
            )
            result = app.acquire_token_for_client(scopes=scopes)

        if "access_token" not in result:
            print("Expected an access token in response. Instead, got the following:")
            print(result)
            raise Exception("Bad response from Azure AD")

        return result["access_token"]

    def call_api(self):
        access_token = self.acquire_token(self.app, self.config["scopes"])

        return requests.get(  # Use token to call downstream service
            self.url,
            headers={"Authorization": "Bearer " + access_token},
            proxies=self.proxies,
            verify=self.requests_ca_bundle,
        )
