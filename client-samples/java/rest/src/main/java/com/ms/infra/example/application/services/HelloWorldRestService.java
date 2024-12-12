package com.ms.infra.example.application.services;

import com.ms.infra.example.application.responseTypes.HelloWorldGetServicesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * This interface defines a RESTful API client for interacting with a Hello World service.
 * It provides methods to perform GET requests to various endpoints.
 */
public interface HelloWorldRestService {
    /**
     * This method performs a GET request to the "services" endpoint.
     * It returns a {@link Call} object that can be used to execute the request.
     * The response type is {@link HelloWorldGetServicesResponse}.
     *
     * @return A {@link Call} object representing the GET request to the "services" endpoint.
     */
    @GET("services")
    Call<HelloWorldGetServicesResponse> getServices();

    /**
     * This method performs a GET request to the "status/{statusCode}" endpoint.
     * The response type is {@code Void}.
     *
     * @param customHeader The custom header to be included in the request.
     * @param statusCode   The path parameter representing the status code.
     * @param delay        The query parameter representing the delay in seconds.
     * @return A {@link Call} object representing the GET request to the "status/{statusCode}" endpoint.
     */
    @GET("status/{statusCode}")
    Call<Void> getStatus(@Header("myCustomHeader") String customHeader,
                         @Path("statusCode") int statusCode,
                         @Query("delay") int delay);
}
