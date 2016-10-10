package com.hp.autonomy.frontend.configuration.redis;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class HostAndPortTest {
    @Test
    public void passesValidation() {
        assertThat(new HostAndPort("example.com", 8080).validate(), is(true));
    }

    @Test
    public void failsValidationForNullPort() {
        assertThat(new HostAndPort("example.com", null).validate(), is(false));
    }

    @Test
    public void failsValidationForNullHost() {
        assertThat(new HostAndPort(null, 8080).validate(), is(false));
    }
}