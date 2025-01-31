MOCK_CONFIG = {
    "client_id": "CLIENT-ID",
    "scopes": ["API-SCOPE"],
    "thumbprint": "CERT-THUMBPRINT",
    "private_key_file": "PRIVATE-KEY-PATH",
    "tenant": "TENANT",
    "proxy_host": "PROXY-HOST",
    "proxy_port": "PROXY-PORT",
    "url": "HTTP://URL/",
    "retry_bad_handshake_status": True,
}

TEST_PROXY_HOST = MOCK_CONFIG["proxy_host"]
TEST_PROXY_PORT = MOCK_CONFIG["proxy_port"]
TEST_PROXY_CONFIG = {
    "http": f"{TEST_PROXY_HOST}:{TEST_PROXY_PORT}",
    "https": f"{TEST_PROXY_HOST}:{TEST_PROXY_PORT}",
}
