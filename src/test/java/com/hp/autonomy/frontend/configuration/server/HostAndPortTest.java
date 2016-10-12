package com.hp.autonomy.frontend.configuration.server;

import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.ConfigurationComponentTest;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class HostAndPortTest extends ConfigurationComponentTest<HostAndPort> {
    @Test(expected = ConfigException.class)
    public void failsValidationForNullPort() throws ConfigException {
        new HostAndPort("example.com", null).basicValidate(null);
    }

    @Test(expected = ConfigException.class)
    public void failsValidationForNullHost() throws ConfigException {
        new HostAndPort(null, 8080).basicValidate(null);
    }

    @Test
    public void toStringTest() {
        assertEquals("example.com:8080", constructComponent().toString());
    }

    @Override
    protected Class<HostAndPort> getType() {
        return HostAndPort.class;
    }

    @Override
    protected HostAndPort constructComponent() {
        return new HostAndPort("example.com", 8080);
    }

    @Override
    protected String sampleJson() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/frontend/configuration/server/hostAndPort.json"));
    }

    @Override
    protected void validateJson(final JsonContent<HostAndPort> json) {
        json.assertThat().extractingJsonPathStringValue("@.host")
                .isEqualTo("example.com");
        json.assertThat().extractingJsonPathNumberValue("@.port")
                .isEqualTo(8080);
    }

    @Override
    protected void validateParsedComponent(final ObjectContent<HostAndPort> component) {
        component.assertThat().hasFieldOrPropertyWithValue("host", "localhost");
        component.assertThat().hasFieldOrPropertyWithValue("port", 1234);
    }

    @Override
    protected void validateMergedComponent(final ObjectContent<HostAndPort> mergedComponent) {
        mergedComponent.assertThat().hasFieldOrPropertyWithValue("host", "example.com");
        mergedComponent.assertThat().hasFieldOrPropertyWithValue("port", 8080);
    }
}