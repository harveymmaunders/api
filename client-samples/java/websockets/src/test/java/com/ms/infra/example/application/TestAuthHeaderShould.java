package com.ms.infra.example.application;

import com.ms.infra.example.application.websocket.AuthHeader;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestAuthHeaderShould {
    public final String BEARER_TOKEN = "TOKEN";
    public final String EXPECTED_AUTH_HEADER = "Bearer " + BEARER_TOKEN;

    @Test
    void return_correct_header() {
        AuthHeader authHeader = new AuthHeader(BEARER_TOKEN);
        assertThat("Incorrect header value", authHeader.getHeaderValue(), is(EXPECTED_AUTH_HEADER));
    }
}
