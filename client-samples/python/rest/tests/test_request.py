import sys
import os
import unittest
from unittest.mock import patch
import requests_mock

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from client_application import call_api
from test_consts import MOCK_CONFIG

URL = "HTTP://URL"
MOCK_TOKEN = "MOCK_TOKEN"
MOCK_BODY_RESPONSE = {"status": "success", "response": "Hello World!"}


class TestApiCall(unittest.TestCase):
    def setUp(self):
        """
        setUp function runs before each unit test.
        Parameters
        ----------
        acquire_token: Mock
            Mock return for the acquire_token method.
        get_client_app_mock: Mock
            Mock return for the get_client_app method.
        """
        # create patches of the acquire_token and get_client_app methods
        self.acquire_token_patch = patch(
            "client_application.acquire_token", return_value=MOCK_TOKEN
        )
        self.get_client_app_patch = patch(
            "client_application.get_client_app", return_value=None
        )

        # Start the patches
        self.acquire_token_patch.start()
        self.get_client_app_patch.start()

    def tearDown(self):
        """
        tearDown function runs after each unit test.
        """
        self.acquire_token_patch.stop()
        self.get_client_app_patch.stop()
        return super().tearDown()

    @requests_mock.Mocker()
    def test_call_api_success(self, mock_request):
        """
        Mock calling API with a successful response.
        Parameters
        ----------
        mock_request: requests_mock.Mocker
            Mock return for the requests get function.
        """
        mock_request.get(URL, json=MOCK_BODY_RESPONSE, status_code=200)

        response = call_api(MOCK_CONFIG)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response.json(), MOCK_BODY_RESPONSE)

    @requests_mock.Mocker()
    def test_call_api_failure(self, mock_request):
        """
        Mock calling API with a failed response.
        Parameters
        ----------
        mock_request: requests_mock.Mocker
            Mock return for the requests get function.
        """
        mock_request.get(URL, json={}, status_code=401)

        response = call_api(MOCK_CONFIG)
        self.assertEqual(response.status_code, 401)
        self.assertEqual(response.json(), {})


if __name__ == "__main__":
    unittest.main()
