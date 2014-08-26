package com.autonomy.frontend.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.fail;

/*
 * $Id:$
 *
 * Copyright (c) 2014, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */
public class CasAuthenticationTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void jsonSerialization() {
        final DefaultLogin defaultLogin = DefaultLogin.generateDefaultLogin();

        final CasConfig casConfig = new CasConfig.Builder()
            .setCasServerLoginUrl("/login/authenticatedLogin")
            .setCasServerUrlPrefix("prefix")
            .setServerName("test-server")
            .build();

        final CasAuthentication casAuthentication = new CasAuthentication.Builder()
            .setCas(casConfig)
            .setDefaultLogin(defaultLogin)
            .setMethod(LoginTypes.CAS)
            .build();

        final JsonNode jsonNode = objectMapper.valueToTree(casAuthentication);

        // hard coding this would prevent package movement
        assertThat(jsonNode.get("className").asText(), is(CasAuthentication.class.getCanonicalName()));
        assertThat(jsonNode.get("method").asText(), is(LoginTypes.CAS));
        assertThat(jsonNode.get("cas").get("casServerLoginUrl").asText(), is("/login/authenticatedLogin"));
        assertThat(jsonNode.get("cas").get("casServerUrlPrefix").asText(), is("prefix"));
        assertThat(jsonNode.get("cas").get("serverName").asText(), is("test-server"));
        assertThat(jsonNode.get("defaultLogin").get("username").asText(), is("admin"));
        assertThat(jsonNode.get("defaultLogin").get("password").asText(), notNullValue());
    }

    @Test
    public void jsonDeserialization() throws IOException {
        final InputStream inputStream = getClass().getResourceAsStream("/com/autonomy/frontend/configuration/casAuthentication.json");

        final TestConfig testConfig = objectMapper.readValue(inputStream, TestConfig.class);
        final Authentication<?> authentication = testConfig.getAuthentication();

        if(authentication instanceof CasAuthentication) {
            final CasAuthentication casAuthentication = (CasAuthentication) authentication;
            final CasConfig cas = casAuthentication.getCas();

            assertThat(cas.getCasServerLoginUrl(), is("loginUrl"));
            assertThat(cas.getCasServerUrlPrefix(), is("prefix"));
            assertThat(cas.getServerName(), is("serverName"));
        }
        else {
            fail("Deserialized class not of correct type");
        }
    }
}
