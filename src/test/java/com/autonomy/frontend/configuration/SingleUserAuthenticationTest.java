package com.autonomy.frontend.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.frontend.configuration.LoginTypes;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Test;
import org.mindrot.jbcrypt.BCrypt;

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

        // hard coding this would prevent package movement
        assertThat(jsonNode.get("className").asText(), is(SingleUserAuthentication.class.getCanonicalName()));
        assertThat(jsonNode.get("method").asText(), is(LoginTypes.SINGLE_USER));
        assertThat(jsonNode.get("singleUser").get("username").asText(), is("admin"));
        assertThat(jsonNode.get("singleUser").get("hashedPassword").asText(), is("$2a$12$uGikZXio88E.bl0A3oEe6eR.bAZxfzyifvQ4pAf6uLflCxUA55ONe"));
        assertThat(jsonNode.get("defaultLogin").get("username").asText(), is("admin"));
        assertThat(jsonNode.get("defaultLogin").get("password").asText(), notNullValue());
    }

    @Test
    public void jsonDeserialization() throws IOException {
        final InputStream inputStream = getClass().getResourceAsStream("/com/autonomy/frontend/configuration/singleUserAuthentication.json");

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
