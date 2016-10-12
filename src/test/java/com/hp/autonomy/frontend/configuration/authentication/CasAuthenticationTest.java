/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.authentication;

import com.hp.autonomy.frontend.configuration.ConfigurationComponentTest;
import com.hp.autonomy.frontend.configuration.LoginTypes;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CasAuthenticationTest extends ConfigurationComponentTest<TestConfig> {
    @Override
    protected Class<TestConfig> getType() {
        return TestConfig.class;
    }

    @Override
    protected TestConfig constructComponent() {
        final UsernameAndPassword defaultLogin = DefaultLogin.generateDefaultLogin();

        final CasConfig casConfig = CasConfig.builder()
                .casServerLoginUrl("/login/authenticatedLogin")
                .casServerUrlPrefix("prefix")
                .serverName("test-server")
                .build();

        return new TestConfig(CasAuthentication.builder()
                .cas(casConfig)
                .defaultLogin(defaultLogin)
                .method(LoginTypes.CAS)
                .build());
    }

    @Override
    protected String sampleJson() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/frontend/configuration/authentication/casAuthentication.json"));
    }

    @Override
    protected void validateJson(final JsonContent<TestConfig> json) {
        json.assertThat().extractingJsonPathStringValue("@.authentication.name").isEqualTo("CasAuthentication");
        json.assertThat().extractingJsonPathStringValue("@.authentication.method").isEqualTo(LoginTypes.CAS);
        json.assertThat().extractingJsonPathStringValue("@.authentication.cas.casServerLoginUrl").isEqualTo("/login/authenticatedLogin");
        json.assertThat().extractingJsonPathStringValue("@.authentication.cas.casServerUrlPrefix").isEqualTo("prefix");
        json.assertThat().extractingJsonPathStringValue("@.authentication.cas.serverName").isEqualTo("test-server");
        json.assertThat().extractingJsonPathStringValue("@.authentication.defaultLogin.username").isEqualTo("admin");
        json.assertThat().extractingJsonPathStringValue("@.authentication.defaultLogin.password").isNotNull();
    }

    @Override
    protected void validateParsedComponent(final ObjectContent<TestConfig> component) {
        @SuppressWarnings("CastToConcreteClass")
        final CasAuthentication casAuthentication = (CasAuthentication) component.getObject().getAuthentication();
        final CasConfig cas = casAuthentication.getCas();

        assertThat(cas.getCasServerLoginUrl(), is("loginUrl"));
        assertThat(cas.getCasServerUrlPrefix(), is("prefix"));
        assertThat(cas.getServerName(), is("serverName"));
    }

    @Override
    protected void validateMergedComponent(final ObjectContent<TestConfig> mergedComponent) {
        @SuppressWarnings("CastToConcreteClass")
        final CasAuthentication casAuthentication = (CasAuthentication) mergedComponent.getObject().getAuthentication();
        final CasConfig cas = casAuthentication.getCas();

        assertThat(cas.getCasServerLoginUrl(), is("/login/authenticatedLogin"));
        assertThat(cas.getCasServerUrlPrefix(), is("prefix"));
        assertThat(cas.getServerName(), is("test-server"));
    }
}
