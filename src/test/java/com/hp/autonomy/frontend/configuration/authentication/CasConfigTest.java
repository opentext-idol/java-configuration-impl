package com.hp.autonomy.frontend.configuration.authentication;

import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.ConfigurationComponentTest;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class CasConfigTest extends ConfigurationComponentTest<CasConfig> {
    @Test(expected = ConfigException.class)
    public void badConfig() throws ConfigException {
        CasConfig.builder().build().basicValidate(null);
    }

    @Override
    protected Class<CasConfig> getType() {
        return CasConfig.class;
    }

    @Override
    protected CasConfig constructComponent() {
        return CasConfig.builder()
                .casServerLoginUrl("/login")
                .casServerUrlPrefix("prefix")
                .serverName("MASTER")
                .build();
    }

    @Override
    protected String sampleJson() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/frontend/configuration/authentication/casConfig.json"));
    }

    @Override
    protected void validateJson(final JsonContent<CasConfig> json) {
        json.assertThat().extractingJsonPathStringValue("@.casServerLoginUrl")
                .isEqualTo("/login");
        json.assertThat().extractingJsonPathStringValue("@.casServerUrlPrefix")
                .isEqualTo("prefix");
        json.assertThat().extractingJsonPathStringValue("@.serverName")
                .isEqualTo("MASTER");
    }

    @Override
    protected void validateParsedComponent(final ObjectContent<CasConfig> component) {
        component.assertThat().hasFieldOrPropertyWithValue("casServerLoginUrl", "/login");
        component.assertThat().hasFieldOrPropertyWithValue("casServerUrlPrefix", "prefix");
        component.assertThat().hasFieldOrPropertyWithValue("serverName", "SLAVE");
    }

    @Override
    protected void validateMergedComponent(final ObjectContent<CasConfig> mergedComponent) {
        mergedComponent.assertThat().hasFieldOrPropertyWithValue("casServerLoginUrl", "/login");
        mergedComponent.assertThat().hasFieldOrPropertyWithValue("casServerUrlPrefix", "prefix");
        mergedComponent.assertThat().hasFieldOrPropertyWithValue("serverName", "MASTER");
    }

    @Override
    protected void validateString(final String objectAsString) {
        assertTrue(objectAsString.contains("serverName"));
    }
}