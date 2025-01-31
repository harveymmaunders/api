import os
import sys
import unittest

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from client_application import load_config, get_proxies
from test_consts import MOCK_CONFIG

TEST_PROXY_HOST = MOCK_CONFIG["proxy_host"]
TEST_PROXY_PORT = MOCK_CONFIG["proxy_port"]
TEST_PROXY_CONFIG = {
    "http": f"{TEST_PROXY_HOST}:{TEST_PROXY_PORT}",
    "https": f"{TEST_PROXY_HOST}:{TEST_PROXY_PORT}",
}


class TestConfigSetup(unittest.TestCase):
    def test_config(self):
        """
        Test that the correct config is loaded.
        """
        config = load_config("./tests/test-config.json")
        assert config == MOCK_CONFIG, "Config loaded correctly"

    def test_proxies(self):
        """
        Test that the correct proxies are loaded.
        """
        proxies = get_proxies(MOCK_CONFIG)
        assert proxies == TEST_PROXY_CONFIG, "Proxies loaded correctly"

    def test_null_proxy_config(self):
        """
        Test that no proxies are loaded when the proxy_host and proxy_port are not provided.
        """
        config = MOCK_CONFIG.copy()
        config.pop("proxy_host")
        config.pop("proxy_port")
        proxies = get_proxies(config)
        assert proxies == None, "Proxies not loaded"


if __name__ == "__main__":
    unittest.main()
