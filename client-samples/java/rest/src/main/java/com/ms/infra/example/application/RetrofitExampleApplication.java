package com.ms.infra.example.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.infra.example.application.morganStanleyServices.MsRetrofitWrapper;
import com.ms.infra.example.application.responseTypes.HelloWorldGetServicesResponse;
import com.ms.infra.example.application.services.HelloWorldRestService;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitExampleApplication {
    private static final Logger logger = LoggerFactory.getLogger(ExampleApplication.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String apiEndpoint = "hello/world/v1/services";

    public static void main(String... args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        // Call the endpoint with retrofit
        callHelloWorldApiWithRetrofit();
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
