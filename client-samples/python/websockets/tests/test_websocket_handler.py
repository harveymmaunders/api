import sys
import os
import time
import subprocess
from unittest.mock import MagicMock
from unittest.mock import patch

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from test_consts import MOCK_CONFIG
from client import create_connection

LOCAL_MOCK_CONFIG = MOCK_CONFIG.copy()
LOCAL_MOCK_CONFIG["proxy_host"] = None
LOCAL_MOCK_CONFIG["proxy_port"] = None
LOCAL_MOCK_CONFIG["retry_bad_handshake_status"] = False

MOCK_TOKEN = "mock_token"

client_api_host = "127.0.0.1"
API_PORT = 13254

API_URL = f"ws://{client_api_host}:{API_PORT}"
# ws://127.0.0.1:12345


def kick_off_subprocess():
    return subprocess.Popen(
        ["venv/Scripts/python.exe", "tests/start_websocket_server.py"]
    )


def test_run_tasks():
    acquire_token_patch = patch("client.acquire_token", return_value=MOCK_TOKEN)
    get_client_app_patch = patch("client.get_client_app", return_value=None)
    get_requests_ca_bundle_patch = patch(
        "client.get_requests_ca_bundle", return_value=None
    )
    acquire_token_patch.start()
    get_client_app_patch.start()
    get_requests_ca_bundle_patch.start()

    websocket_handler = create_connection(LOCAL_MOCK_CONFIG, API_URL)
    websocket_handler.on_open = MagicMock()
    websocket_handler.on_message = MagicMock()

    process = kick_off_subprocess()
    time.sleep(2)

    websocket_handler.connect(retry=False)
    time.sleep(2)

    process.terminate()  # ensure the subprocess has stopped running

    websocket_handler.on_open.assert_called_once()
    websocket_handler.on_message.assert_called_once()

    acquire_token_patch.stop()
    get_client_app_patch.stop()
    get_requests_ca_bundle_patch.stop()
