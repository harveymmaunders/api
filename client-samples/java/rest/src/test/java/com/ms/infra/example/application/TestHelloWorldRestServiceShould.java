package com.ms.infra.example.application;

import com.ms.infra.example.application.interceptors.AuthHeaderInterceptor;
import com.ms.infra.example.application.morganStanleyServices.MsClientAuthTokenService;
import com.ms.infra.example.application.morganStanleyServices.MsRetrofitWrapper;
import com.ms.infra.example.application.responseTypes.HelloWorldGetServicesResponse;
import com.ms.infra.example.application.services.HelloWorldRestService;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TestHelloWorldRestServiceShould {
    private MockWebServer mockWebServer;
    private HelloWorldRestService helloWorldRestService;
    private final String EXAMPLE_GET_SERVICE_RESPONSE = "{\"response\":\"value\",\"time\":\"2022-01-01T00:00:00Z\"}";
    private final String RESPONSE_VALUE = "value";
    private final String TIME_VALUE = "2022-01-01T00:00:00Z";
    private final String MOCK_401_ERROR_RESPONSE = "{ \"statusCode\": 401, \"message\": \"Authentication required\" }";
    private final String MOCK_BEARER_TOKEN = "MOCK_BEARER_TOKEN";
    private final String EXPECTED_ENDPOINT = "/hello/world/v1/services";

    @BeforeEach
    void setup_hello_world_rest_service() throws IOException {
        // Create a mock web server
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Mock MsClientAuthTokenService to return fake token
        MsClientAuthTokenService mockMsClientAuthTokenService = Mockito.mock(MsClientAuthTokenService.class);
        Mockito.when(mockMsClientAuthTokenService.getAccessToken()).thenReturn(MOCK_BEARER_TOKEN);
        AuthHeaderInterceptor authHeaderInterceptor = new AuthHeaderInterceptor(mockMsClientAuthTokenService);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(authHeaderInterceptor).build();

        MsRetrofitWrapper msRetrofitWrapper = new MsRetrofitWrapper(mockWebServer.url("/"), okHttpClient);
        helloWorldRestService = msRetrofitWrapper.createService(HelloWorldRestService.class);
    }

    @AfterEach
    void end_mock_web_server() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void successfully_call_api() throws IOException, InterruptedException {
        // Create a mock response
        MockResponse mockResponse = new MockResponse()
            .setResponseCode(200)
            .setBody(EXAMPLE_GET_SERVICE_RESPONSE)
            .addHeader("Content-Type", "application/json");

        // Enqueue the mock response
        mockWebServer.enqueue(mockResponse);

        Call<HelloWorldGetServicesResponse> call = helloWorldRestService.getServices();

        // Execute the call and get the response
        Response<HelloWorldGetServicesResponse> response = call.execute();

        // Verify the response
        assertThat("Incorrect Response code", response.code(), is(200));

        HelloWorldGetServicesResponse data = response.body();
        assertThat("Incorrect value response", data.getResponse(), is(RESPONSE_VALUE));
        assertThat("Incorrect time response", data.getTime(), is(TIME_VALUE));

        // Check the request
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat("Incorrect path called",  recordedRequest.getPath(), is(EXPECTED_ENDPOINT));
        assertThat("Incorrect auth header", recordedRequest.getHeader("Authorization"), is("Bearer " + MOCK_BEARER_TOKEN));
    }

    @Test
    void deal_with_error_call() throws IOException {
        // Create a mock error response
        MockResponse mockResponse = new MockResponse()
            .setResponseCode(401)
            .setBody(MOCK_401_ERROR_RESPONSE);

        // Enqueue the mock response
        mockWebServer.enqueue(mockResponse);

        Call<HelloWorldGetServicesResponse> call = helloWorldRestService.getServices();
        // Execute the call and get the response
        Response<HelloWorldGetServicesResponse> response = call.execute();

        // Verify the response
        assertThat("Incorrect Response code", response.code(), is(401));
    }
}
