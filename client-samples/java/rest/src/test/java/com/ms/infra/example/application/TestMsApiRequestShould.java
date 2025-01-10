package com.ms.infra.example.application;

import com.ms.infra.example.application.interceptors.AuthHeaderInterceptor;
import com.ms.infra.example.application.morganStanleyServices.MsApiRequest;
import com.ms.infra.example.application.morganStanleyServices.MsClientAuthTokenService;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TestMsApiRequestShould {
    private OkHttpClient okHttpClient;
    private MockWebServer mockWebServer;

    private final String MOCK_BEARER_TOKEN = "MOCK_BEARER_TOKEN";
    private final String TEST_ENDPOINT = "hello/world/v1/services";
    private final String EXPECTED_ENDPOINT = "/" + TEST_ENDPOINT;
    @BeforeEach
    public void setupOkHttpClient() throws MalformedURLException {
        // Mock MsClientAuthTokenService to return fake token
        MsClientAuthTokenService mockMsClientAuthTokenService = Mockito.mock(MsClientAuthTokenService.class);
        Mockito.when(mockMsClientAuthTokenService.getAccessToken()).thenReturn(MOCK_BEARER_TOKEN);
        AuthHeaderInterceptor authHeaderInterceptor = new AuthHeaderInterceptor(mockMsClientAuthTokenService);

        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(authHeaderInterceptor).build();
    }

    @BeforeEach
    public void setupMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    public void shutdownMockWebServer() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void call_api() throws InterruptedException, IOException {
        // Create a mock response
        MockResponse mockResponse = new MockResponse();
        mockWebServer.enqueue(mockResponse);

        System.out.println(mockWebServer.url("/"));
        MsApiRequest msApiRequest = new MsApiRequest(okHttpClient, mockWebServer.url("").toString());

        msApiRequest.callEndpoint(TEST_ENDPOINT);

        // Check the request
        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat("Incorrect path called",  recordedRequest.getPath(), is(EXPECTED_ENDPOINT));
        assertThat("Incorrect auth header", recordedRequest.getHeader("Authorization"), is("Bearer " + MOCK_BEARER_TOKEN));
    }
}
