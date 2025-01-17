package com.ms.infra.example.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.infra.example.application.config.MicroprofileConfigService;
import com.ms.infra.example.application.interceptors.AuthHeaderInterceptor;
import com.ms.infra.example.application.morganStanleyServices.MsClientAuthTokenService;
import com.ms.infra.example.application.morganStanleyServices.MsRetrofitWrapper;
import com.ms.infra.example.application.responseTypes.HelloWorldGetServicesResponse;
import com.ms.infra.example.application.services.HelloWorldRestService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExampleApplication {
    private static final Logger logger = LoggerFactory.getLogger(ExampleApplication.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String apiEndpoint = "hello/world/v1/services";

    public static void main(String... args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        // Call the endpoint with an okHttpClient request
        callHelloWorldApi();

        // Call the endpoint with retrofit
        callHelloWorldApiWithRetrofit();
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

    public static void callHelloWorldApiWithRetrofit() throws Exception {
        MsRetrofitWrapper msRetrofitWrapper = new MsRetrofitWrapper(HttpLoggingInterceptor.Level.BODY);
        HelloWorldRestService helloWorldRestService = msRetrofitWrapper.createService(HelloWorldRestService.class);

        Call<HelloWorldGetServicesResponse> call = helloWorldRestService.getServices();

        // this function will make an API call
        call.enqueue(new Callback<HelloWorldGetServicesResponse>() {
            // this will run if the API call returns any response
            // it acts similar to a try catch, and will fail over to the onFailure method is anything errors
            @Override
            public void onResponse(Call<HelloWorldGetServicesResponse> call, Response<HelloWorldGetServicesResponse> response) {
                if (response.isSuccessful()) {
                    // load the response body into our own custom class
                    HelloWorldGetServicesResponse data = response.body();
                    try {
                        logger.info("Response code: {}", response.code());
                        logger.info(objectMapper.writeValueAsString(data));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    // there was a response but was not successful
                    logger.error("Response was not successful, response code: {}", response.code());
                }
            }

            @Override
            public void onFailure(Call<HelloWorldGetServicesResponse> call, Throwable t) {
                // the trace error from onResponse is passed into this class as Throwable t
                logger.error("Failure with response, issue: {}", t.toString());
            }
        });
    }
}
