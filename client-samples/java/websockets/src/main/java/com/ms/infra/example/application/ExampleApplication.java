package com.ms.infra.example.application;

import com.ms.infra.example.application.websocket.WebSocketService;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleApplication {
    private static final Logger logger = LoggerFactory.getLogger(ExampleApplication.class);

    public static void main(String... args) throws Exception {
        connectToWebsocketApi();
    }

    public static void connectToWebsocketApi() throws Exception {
        WebSocketService webSocketService = new WebSocketService(HttpLoggingInterceptor.Level.BODY);
        webSocketService.connect();
    }
}
