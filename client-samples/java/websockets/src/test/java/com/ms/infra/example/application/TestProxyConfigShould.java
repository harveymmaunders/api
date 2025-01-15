package com.ms.infra.example.application;

import com.ms.infra.example.application.config.MicroprofileConfigService;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestProxyConfigShould {
    @BeforeEach
    void reset_proxy_properties() {
        System.clearProperty("https.proxyHost");
        System.clearProperty("https.proxyPort");
    }

    @Test
    void set_proxy_as_null_if_not_set_in_config() {
        // Mock config provider to return empty optional rather than value
        Config configProviderMock = mock(ConfigProvider.getConfig().getClass());
        Optional<String> emptyOptional = Optional.empty();
        when(configProviderMock.getOptionalValue("proxy-name", String.class)).thenReturn(emptyOptional);
        when(configProviderMock.getOptionalValue("proxy-port", String.class)).thenReturn(emptyOptional);

        MicroprofileConfigService.setProxy(configProviderMock);
        assertThat("There should be no proxy set", System.getProperty("https.proxyHost"), is(nullValue()));
        assertThat("There should be no proxy set", System.getProperty("https.proxyHost"), is(nullValue()));
    }
}
