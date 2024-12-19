import os
import sys
import unittest
from unittest.mock import patch


sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from MsRequestsWrapper import MsRequestsWrapper

MOCK_CONFIG = {
    "client_id": "CLIENT-ID",
    "scopes": ["API-SCOPE"],
    "thumbprint": "CERT-THUMBPRINT",
    "private_key_file": "PRIVATE-KEY-PATH",
    "tenant": "TENANT",
    "proxy_host": "PROXY-HOST",
    "proxy_port": "PROXY-PORT",
    "url": "HTTP://URL/",
}

TEST_PROXY_HOST = MOCK_CONFIG["proxy_host"]
TEST_PROXY_PORT = MOCK_CONFIG["proxy_port"]
TEST_PROXY_CONFIG = {
    "http": f"{TEST_PROXY_HOST}:{TEST_PROXY_PORT}",
    "https": f"{TEST_PROXY_HOST}:{TEST_PROXY_PORT}",
}


class TestConfigSetup(unittest.TestCase):
    @patch.object(MsRequestsWrapper, "get_client_app")
    def setUp(self, get_client_app_mock):
        """
        setUp function runs before each unit test.

        Parameters
        ----------
        load_private_key_mock: Mock
            Mock object for the load_private_key method.
        """
        get_client_app_mock.return_value = None
        self.ms_request_wrapper = MsRequestsWrapper("./tests/test-config.json")

    def tearDown(self):
        """
        tearDown function runs after each unit test.
        """
        return super().tearDown()

    def test_config(self):
        """
        Test that the correct config is loaded.
        """
        config = self.ms_request_wrapper.load_config("./tests/test-config.json")
        assert config == MOCK_CONFIG, "Config loaded correctly"

    def test_proxies(self):
        """
        Test that the correct proxies are loaded.
        """
        proxies = self.ms_request_wrapper.get_proxies(MOCK_CONFIG)
        assert proxies == TEST_PROXY_CONFIG, "Proxies loaded correctly"

    def test_null_proxy_config(self):
        """
        Test that no proxies are loaded when the proxy_host and proxy_port are not provided.
        """
        config = MOCK_CONFIG.copy()
        config.pop("proxy_host")
        config.pop("proxy_port")
        proxies = self.ms_request_wrapper.get_proxies(config)
        assert proxies == None, "Proxies not loaded"


if __name__ == "__main__":
    unittest.main()
