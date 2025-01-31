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

import java.time.Duration;

/**
 * WebSocketService class to connect to WebSocket API and handle WebSocket events.
 */
public class WebSocketService extends WebSocketListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);

    private WebSocket webSocket;
    private final OkHttpClient client;
    private final String url;
    private final MsClientAuthTokenService msClientAuthTokenService;

    /**
     * Constructor for WebSocketService.
     * @param logLevel
     * @throws Exception
     */
    public WebSocketService(Level logLevel) throws Exception {
        MicroprofileConfigService microprofileConfigService = new MicroprofileConfigService();
        msClientAuthTokenService = new MsClientAuthTokenService(microprofileConfigService);
        this.client = getOkHttpClient(logLevel);
        this.url = microprofileConfigService.getApiUrl();
    }

    /**
     * Constructor for WebSocketService.
     * @param microprofileConfigService
     * @param url
     * @param logLevel
     * @throws Exception
     */
    public WebSocketService(MicroprofileConfigService microprofileConfigService, String url, Level logLevel) throws Exception {
        msClientAuthTokenService = new MsClientAuthTokenService(microprofileConfigService);
        this.client = getOkHttpClient(logLevel);
        this.url = url;
    }

    /**
     * Constructor for WebSocketService.
     * @param url
     * @param msClientAuthTokenService
     * @throws Exception
     */
    public WebSocketService(String url, MsClientAuthTokenService msClientAuthTokenService) throws Exception {
        this.msClientAuthTokenService = msClientAuthTokenService;
        this.client = getOkHttpClient(HttpLoggingInterceptor.Level.BODY);
        this.url = url;
    }

    /**
     * Setup OkHttpClient with logging interceptor.
     * @param logLevel
     * @return OkHttpClient
     */
    public OkHttpClient getOkHttpClient(Level logLevel) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(logLevel);

        return new OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .pingInterval(Duration.ofSeconds(30))
            .build();
    }

    /**
     * Logic for closing the websocket connection.
     */
    @Override
    public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        logger.info("Websocket closing, code: {}", code);
        webSocket.close(1000, null);
    }

    /**
     * Logic for handling errors during websocket connection.
     */
    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        logger.error("Error during WebSocket connection", t);
        webSocket.close(1000, null);
        connect();
    }

    /**
     * Logic for handling closed websocket connection.
     */
    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        logger.warn("Connection closed. Reconnecting...");
        connect();
    }

    /**
     * Logic for handling messages received from websocket.
     */
    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        logger.info("Received message: {}", text);
    }

    /**
     * Logic for handling opened websocket connection.
     */
    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        logger.info("Opened connection.");
    }

    /**
     * Get bearer token
     * 
     * If there is an issue getting the token, then it will return an empty token, leading to a 401 error.
     * @return bearer token
     */
    public String getAuthToken() {
        String token = "";
        try {
            token = msClientAuthTokenService.getAccessToken();
        } catch (Exception e) {
            logger.error("Error retrieving JWT.", e);
        }
        return token;
    }

    /**
     * Connect to websocket
     * @return websocket
    */
    public WebSocket connect() {
        AuthHeader authHeader = new AuthHeader(getAuthToken());
        logger.info("Connecting to WebSocket...");
        Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", authHeader.getHeaderValue())
            .build();
        this.webSocket = client.newWebSocket(request, this);
        return this.webSocket;
    }
}
