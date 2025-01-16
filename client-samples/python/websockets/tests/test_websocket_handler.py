import sys
import os
import subprocess
from time import sleep
import unittest
from unittest.mock import MagicMock
from unittest.mock import patch

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from test_consts import MOCK_CONFIG
from client import create_connection

LOCAL_MOCK_CONFIG = MOCK_CONFIG.copy()
LOCAL_MOCK_CONFIG["proxy_host"] = None
LOCAL_MOCK_CONFIG["proxy_port"] = None

MOCK_TOKEN = "mock_token"

API_HOST = "127.0.0.1"
API_PORT = 13254
API_URL = f"ws://{API_HOST}:{API_PORT}"


class TestApiCall(unittest.TestCase):
    def setUp(self):
        """
        setUp function runs before each unit test.
        Sets up patches
        """
        self.acquire_token_patch = patch(
            "client.acquire_token", return_value=MOCK_TOKEN
        )
        self.get_client_app_patch = patch("client.get_client_app", return_value=None)
        self.get_requests_ca_bundle_patch = patch(
            "client.get_requests_ca_bundle", return_value=None
        )
        self.acquire_token_patch.start()
        self.get_client_app_patch.start()
        self.get_requests_ca_bundle_patch.start()

    def tearDown(self):
        """
        tearDown function runs after each unit test.
        """
        self.acquire_token_patch.stop()
        self.get_client_app_patch.stop()
        self.get_requests_ca_bundle_patch.stop()
        return super().tearDown()

    def start_server(self):
        """
        Start the websocket server.
        """
        return subprocess.Popen([sys.executable, "tests/start_websocket_server.py"])

    def test_run_tasks(self):
        websocket_handler = create_connection(LOCAL_MOCK_CONFIG, API_URL)
        websocket_handler.on_open = MagicMock()
        websocket_handler.on_message = MagicMock()

        process = self.start_server()
        sleep(2)
        websocket_handler.connect(retry=False)

        process.terminate()  # ensure the subprocess has stopped running

        websocket_handler.on_open.assert_called_once()
        websocket_handler.on_message.assert_called_once()
