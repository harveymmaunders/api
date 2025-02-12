package com.ms.infra.example.application;

import com.ms.infra.example.application.config.MicroprofileConfigService;
import com.ms.infra.example.application.interceptors.AuthHeaderInterceptor;
import com.ms.infra.example.application.morganStanleyServices.MsClientAuthTokenService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleApplication {
    private static final Logger logger = LoggerFactory.getLogger(ExampleApplication.class);
    private static final String apiEndpoint = "hello/world/v1/services";

    public static void main(String... args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        callHelloWorldApi();
    }

    public static void callHelloWorldApi() throws Exception {
        // Class to read config properties
        MicroprofileConfigService microprofileConfigService = new MicroprofileConfigService();

        // Get auth token
        MsClientAuthTokenService msClientAuthTokenService = new MsClientAuthTokenService(microprofileConfigService);

        // Logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Add auth token to every request
        AuthHeaderInterceptor authHeaderInterceptor = new AuthHeaderInterceptor(msClientAuthTokenService);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authHeaderInterceptor)
                .build();

        Request request = new Request.Builder()
                .url(microprofileConfigService.getMsUrlDomain() + apiEndpoint)
                .build();

        // Execute request and log response
        okhttp3.Response response = okHttpClient.newCall(request).execute();
        logger.info("Response code: {}", response.code());
        logger.info("Response: {}", response.body().string());
    }
}
