package com.ms.infra.example.application;

import com.ms.infra.example.application.interceptors.AuthHeaderInterceptor;
import com.ms.infra.example.application.morganStanleyServices.MsClientAuthTokenService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import okhttp3.mockwebserver.RecordedRequest;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAuthHeaderInterceptorShould {
    MsClientAuthTokenService msClientAuthTokenServiceMock;
    private final String MOCK_TOKEN = "mock-token";

    @Rule
    public MockWebServer mockServer = new MockWebServer();

    @BeforeEach
    void mock_ms_client_auth_token_service() throws MalformedURLException {
        msClientAuthTokenServiceMock = Mockito.mock(MsClientAuthTokenService.class);
        Mockito.when(msClientAuthTokenServiceMock.getAccessToken()).thenReturn(MOCK_TOKEN);
    }

    @Test
    void add_auth_header_to_call() throws IOException, InterruptedException {
        mockServer.enqueue(new MockResponse().setBody("Test call"));

        OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new AuthHeaderInterceptor(msClientAuthTokenServiceMock))
            .build();

        Request request = new Request.Builder()
            .url(mockServer.url("/"))
            .build();

        client.newCall(request).execute();
        RecordedRequest recordedRequest = mockServer.takeRequest();
        assertEquals(recordedRequest.getHeader("Authorization"), "Bearer " + MOCK_TOKEN);
    }
}
