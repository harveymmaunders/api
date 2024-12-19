import sys
import os
import unittest
from unittest.mock import patch, Mock
import requests_mock

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), "..")))

from MsRequestsWrapper import MsRequestsWrapper

URL = "HTTP://URL"
MOCK_TOKEN = "MOCK_TOKEN"
MOCK_BODY_RESPONSE = {"status": "success", "response": "Hello World!"}


class TestApiCall(unittest.TestCase):
    @patch.object(MsRequestsWrapper, "get_client_app")
    def setUp(self, get_client_app_mock):
        """
        setUp function runs before each unit test.

        Parameters
        ----------
        get_client_app_mock: Mock
            Mock return for the get_client_app method.
        """
        get_client_app_mock.return_value = None
        self.ms_request_wrapper_mock = MsRequestsWrapper("./tests/test-config.json")
        self.ms_request_wrapper_mock.acquire_token = Mock(return_value=MOCK_TOKEN)

    def tearDown(self):
        """
        tearDown function runs after each unit test.
        """
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

        response = self.ms_request_wrapper_mock.call_api()
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

        response = self.ms_request_wrapper_mock.call_api()
        self.assertEqual(response.status_code, 401)
        self.assertEqual(response.json(), {})


if __name__ == "__main__":
    unittest.main()
