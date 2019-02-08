package org.zalando.riptide.autoconfigure;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.zalando.riptide.Http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

@SpringBootTest(classes = DefaultTestConfiguration.class, webEnvironment = NONE)
@TestPropertySource(properties = {
    "riptide.defaults.connect-timeout: 1 second",
    "riptide.defaults.socket-timeout: 2 seconds",
    "riptide.defaults.connection-time-to-live: 1 minute",
    "riptide.defaults.max-connections-per-route: 12",
    "riptide.defaults.max-connections-total: 12",
    "riptide.clients.example.connect-timeout: 12 minutes",
    "riptide.clients.example.socket-timeout: 34 hours",
    "riptide.clients.example.connection-time-to-live: 1 day",
    "riptide.clients.example.max-connections-per-route: 24",
    "riptide.clients.example.max-connections-total: 24",
})
@Component
final class ClientConfigurationTest {

    @Autowired
    @Qualifier("example")
    private Http exampleRest;

    @Autowired
    @Qualifier("ecb")
    private Http ecbRest;

    @Autowired
    @Qualifier("example")
    private RestTemplate exampleRestTemplate;

    @Autowired
    @Qualifier("example")
    private AsyncRestTemplate exampleAsyncRestTemplate;

    @Autowired
    @Qualifier("example")
    private HttpClient exampleHttpClient;

    @Test
    void shouldWireOAuthCorrectly() {
        assertThat(exampleRest, is(notNullValue()));
        assertThat(exampleRestTemplate, is(notNullValue()));
        assertThat(exampleAsyncRestTemplate, is(notNullValue()));
    }

    @Test
    void shouldWireNonOAuthCorrectly() {
        assertThat(ecbRest, is(notNullValue()));
    }

    @Test
    void shouldApplyTimeouts() {
        assertThat("Configurable http client expected", exampleHttpClient, is(instanceOf(Configurable.class)));

        final RequestConfig config = ((Configurable) exampleHttpClient).getConfig();

        assertThat(config.getSocketTimeout(), is(34 * 60 * 60 * 1000));
        assertThat(config.getConnectTimeout(), is(12 * 60 * 1000));
    }

}
