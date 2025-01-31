package com.ms.infra.example.application.responseTypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * After making an API call, the data from the response body will be loaded into this class.
 * API: Hello World API
 * Type: GET Request
 * Endpoint: Services
 */
public class HelloWorldGetServicesResponse {
    /**
     * Stores value for response key
     */
    private String response;

    /**
     * Stores value for time key
     */
    private String time;

    /**
     * Constructor
     * Values from API response body are automatically passed into this constructor
     * @param response response value
     * @param time time value
     */
    @JsonCreator
    public HelloWorldGetServicesResponse(@JsonProperty("response") String response, @JsonProperty("time") String time) {
        // this is the class which the response body will be loaded into
        this.response = response;
        this.time = time;
    }

    /**
     * Get response value
     * @return response value
     */
    public String getResponse() {
        return this.response;
    }

    /**
     * Get time value
     * @return time value
     */
    public String getTime() {
        return this.time;
    }
}
