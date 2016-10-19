package com.hp.autonomy.frontend.configuration.authentication;

import com.hp.autonomy.frontend.configuration.ConfigurationComponentTest;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;

public class UsernameAndPasswordTest extends ConfigurationComponentTest<UsernameAndPassword> {
    @Override
    protected Class<UsernameAndPassword> getType() {
        return UsernameAndPassword.class;
    }

    @Override
    protected UsernameAndPassword constructComponent() {
        return new UsernameAndPassword("Getafix", "Abracadabra");
    }

    @Override
    protected String sampleJson() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/frontend/configuration/authentication/usernameAndPassword.json"));
    }

    @Override
    protected void validateJson(final JsonContent<UsernameAndPassword> json) {
        json.assertThat().extractingJsonPathStringValue("@.username")
                .isEqualTo("Getafix");
        json.assertThat().extractingJsonPathStringValue("@.password")
                .isEqualTo("Abracadabra");
    }

    @Override
    protected void validateParsedComponent(final ObjectContent<UsernameAndPassword> component) {
        component.assertThat().hasFieldOrPropertyWithValue("username", "Sinbad the Sailor");
        component.assertThat().hasFieldOrPropertyWithValue("password", "Open Sesame");
    }

    @Override
    protected void validateMergedComponent(final ObjectContent<UsernameAndPassword> mergedComponent) {
        mergedComponent.assertThat().hasFieldOrPropertyWithValue("username", "Getafix");
        mergedComponent.assertThat().hasFieldOrPropertyWithValue("password", "Abracadabra");
    }

    @Override
    protected void validateString(final String objectAsString) {
    }
}