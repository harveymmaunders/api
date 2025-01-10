package com.ms.infra.example.application.morganStanleyServices;

import com.ms.infra.example.application.ExampleApplication;
import com.ms.infra.example.application.config.MicroprofileConfigService;
import com.ms.infra.example.application.interceptors.AuthHeaderInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MsApiRequest {
    /**
     * Logging
     */
    private static final Logger logger = LoggerFactory.getLogger(ExampleApplication.class);

    /**
     * Morgan Stanley Url Domain
     */
    private final String urlDomain;

    /**
     * Configured retrofit instance
     */
    private final OkHttpClient okHttpClient;

    /**
     * Constructor
     * @param logLevel Log interceptor level
     * @throws Exception throws exception if error when creating msClientAuthTokenService
     */
    public MsApiRequest(HttpLoggingInterceptor.Level logLevel) throws Exception {
        MicroprofileConfigService microprofileConfigService = new MicroprofileConfigService();
        MsClientAuthTokenService msClientAuthTokenService = new MsClientAuthTokenService(microprofileConfigService);
        this.urlDomain = microprofileConfigService.getMsUrlDomain();
        this.okHttpClient = getOkHttpClient(logLevel, msClientAuthTokenService);
    }

    /**
     * Constructor for custom okHttpClient
     * @param okHttpClient
     * @param urlDomain
     */
    public MsApiRequest(OkHttpClient okHttpClient, String urlDomain) {
        this.okHttpClient = okHttpClient;
        this.urlDomain = urlDomain;
    }

    /**
     * This method configures the OkHttp Client.
     * It adds logging and authorization interceptor.
     * @param logLevel log level
     * @return configured okhttp client
     */
    public OkHttpClient getOkHttpClient(HttpLoggingInterceptor.Level logLevel, MsClientAuthTokenService msClientAuthTokenService) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(logLevel);

        AuthHeaderInterceptor authHeaderInterceptor = new AuthHeaderInterceptor(msClientAuthTokenService);

        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authHeaderInterceptor)
                .build();
    }

    /**
     * Call Morgan Stanley API and log output
     * @param apiEndpoint api endpoint to call
     * @throws IOException
     */
    public void callEndpoint(String apiEndpoint) throws IOException {
        Request request = new Request.Builder()
                .url(urlDomain + apiEndpoint)
                .build();
        // Execute request and handle response
        Response response = okHttpClient.newCall(request).execute();

        logger.info("Response code: {}", response.code());
        logger.info("Response: {}", response.body().string());
    }
}
