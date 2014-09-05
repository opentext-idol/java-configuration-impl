/*
 * Copyright 2013-2014 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

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

public class SingleUserAuthenticationTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void jsonSerialization() {
        final DefaultLogin defaultLogin = DefaultLogin.generateDefaultLogin();

        final BCryptUsernameAndPassword singleUser = new BCryptUsernameAndPassword.Builder()
            .setUsername("admin")
            .setHashedPassword("$2a$12$uGikZXio88E.bl0A3oEe6eR.bAZxfzyifvQ4pAf6uLflCxUA55ONe")
            .build();

        final SingleUserAuthentication singleUserAuthentication = new SingleUserAuthentication.Builder()
            .setSingleUser(singleUser)
            .setDefaultLogin(defaultLogin)
            .setMethod(LoginTypes.SINGLE_USER)
            .build();

        final JsonNode jsonNode = objectMapper.valueToTree(singleUserAuthentication);

        assertThat(jsonNode.get("name").asText(), is("SingleUserAuthentication"));
        assertThat(jsonNode.get("method").asText(), is(LoginTypes.SINGLE_USER));
        assertThat(jsonNode.get("singleUser").get("username").asText(), is("admin"));
        assertThat(jsonNode.get("singleUser").get("hashedPassword").asText(), is("$2a$12$uGikZXio88E.bl0A3oEe6eR.bAZxfzyifvQ4pAf6uLflCxUA55ONe"));
        assertThat(jsonNode.get("defaultLogin").get("username").asText(), is("admin"));
        assertThat(jsonNode.get("defaultLogin").get("password").asText(), notNullValue());
    }

    @Test
    public void jsonDeserialization() throws IOException {
        final InputStream inputStream = getClass().getResourceAsStream("/com/hp/autonomy/frontend/configuration/singleUserAuthentication.json");

        final TestConfig testConfig = objectMapper.readValue(inputStream, TestConfig.class);
        final Authentication<?> authentication = testConfig.getAuthentication();

        if(authentication instanceof SingleUserAuthentication) {
            final SingleUserAuthentication singleUserAuthentication = (SingleUserAuthentication) authentication;
            final BCryptUsernameAndPassword singleUser = singleUserAuthentication.getSingleUser();

            assertThat(singleUser.getUsername(), is("admin"));
            assertThat(singleUser.getHashedPassword(), is("$2a$12$uGikZXio88E.bl0A3oEe6eR.bAZxfzyifvQ4pAf6uLflCxUA55ONe"));
        }
        else {
            fail("Deserialized class not of correct type");
        }
    }
}
