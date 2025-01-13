package com.ms.infra.example.application.websocket;

import com.ms.infra.example.application.config.MicroprofileConfigService;
import com.ms.infra.example.application.morganStanleyServices.MsClientAuthTokenService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.logging.HttpLoggingInterceptor.Level;

import java.net.MalformedURLException;
import java.time.Duration;


public class WebSocketService extends WebSocketListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);

    private WebSocket webSocket;
    private final OkHttpClient client;
    private final String url;
    private final MsClientAuthTokenService msClientAuthTokenService;

    public WebSocketService(Level logLevel) throws Exception {
        MicroprofileConfigService microprofileConfigService = new MicroprofileConfigService();
        msClientAuthTokenService = new MsClientAuthTokenService(microprofileConfigService);
        this.client = getOkHttpClient(logLevel);
        this.url = microprofileConfigService.getApiUrl();
    }

    public WebSocketService(MicroprofileConfigService microprofileConfigService, String url, Level logLevel) throws Exception {
        msClientAuthTokenService = new MsClientAuthTokenService(microprofileConfigService);
        this.client = getOkHttpClient(logLevel);
        this.url = url;
    }

    public WebSocketService(String url, MsClientAuthTokenService msClientAuthTokenService) throws Exception {
        this.msClientAuthTokenService = msClientAuthTokenService;
        this.client = getOkHttpClient(HttpLoggingInterceptor.Level.BODY);
        this.url = url;
    }

    public OkHttpClient getOkHttpClient(Level logLevel) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(logLevel);

        return new OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .pingInterval(Duration.ofSeconds(30))
            .build();
    }

    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        logger.info("Websocket closing, code: {}", code);
        webSocket.close(1000, null);
    }

    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        logger.error("Error during WebSocket connection", t);
        webSocket.close(1000, null);
        connect();
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        logger.warn("Connection closed. Reconnecting...");
        connect();
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        logger.info("Received message: {}", text);
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        logger.info("Opened connection.");
    }

    public String getAuthToken() {
        String token = null;
        try {
            token = msClientAuthTokenService.getAccessToken();
        } catch (MalformedURLException e) {
            logger.error("Error retrieving JWT.", e);
        }
        return token;
    }

    public WebSocket connect() {
        AuthHeader authHeader = new AuthHeader(getAuthToken());
        logger.info("Connecting to WebSocket...");
        Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", authHeader.getHeaderValue())
            .build();
        webSocket = client.newWebSocket(request, this);
        return webSocket;
    }
}
