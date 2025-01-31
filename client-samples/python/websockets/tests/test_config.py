import os
import sys
import unittest

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from client import load_config, get_proxies
from test_consts import MOCK_CONFIG, TEST_PROXY_CONFIG


class TestConfigSetup(unittest.TestCase):
    def test_config(self):
        """
        Test that the correct config is loaded.
        """
        config = load_config("./tests/test-config.json")
        assert config == MOCK_CONFIG, "Incorrect config values"

    def test_proxies(self):
        """
        Test that the correct proxies are loaded.
        """
        proxies = get_proxies(MOCK_CONFIG)
        assert proxies == TEST_PROXY_CONFIG, "Incorrect proxy values"

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
