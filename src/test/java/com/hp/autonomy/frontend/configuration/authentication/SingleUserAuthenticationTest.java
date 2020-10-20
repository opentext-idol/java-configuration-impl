/*
 * (c) Copyright 2013-2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("CastToConcreteClass")
public class SingleUserAuthenticationTest extends ConfigurationComponentTest<TestConfig> {
    @Override
    protected Class<TestConfig> getType() {
        return TestConfig.class;
    }

    @Override
    protected TestConfig constructComponent() {
        final UsernameAndPassword defaultLogin = DefaultLogin.generateDefaultLogin();

        final BCryptUsernameAndPassword singleUser = BCryptUsernameAndPassword.builder()
                .username("admin")
                .hashedPassword("$2a$12$uGikZXio88E.bl0A3oEe6eR.bAZxfzyifvQ4pAf6uLflCxUA55ONe")
                .build();

        return new TestConfig(SingleUserAuthentication.builder()
                .singleUser(singleUser)
                .defaultLogin(defaultLogin)
                .method(LoginTypes.SINGLE_USER)
                .build());
    }

    @Override
    protected String sampleJson() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/frontend/configuration/authentication/singleUserAuthentication.json"));
    }

    @Override
    protected void validateJson(final JsonContent<TestConfig> json) {
        json.assertThat().extractingJsonPathStringValue("@.authentication.name").isEqualTo("SingleUserAuthentication");
        json.assertThat().extractingJsonPathStringValue("@.authentication.method").isEqualTo(LoginTypes.SINGLE_USER);
        json.assertThat().extractingJsonPathStringValue("@.authentication.singleUser.username").isEqualTo("admin");
        json.assertThat().extractingJsonPathStringValue("@.authentication.singleUser.hashedPassword").isEqualTo("$2a$12$uGikZXio88E.bl0A3oEe6eR.bAZxfzyifvQ4pAf6uLflCxUA55ONe");
        json.assertThat().extractingJsonPathStringValue("@.authentication.defaultLogin.username").isEqualTo("admin");
        json.assertThat().extractingJsonPathStringValue("@.authentication.defaultLogin.password").isNotNull();
    }

    @Override
    protected void validateParsedComponent(final ObjectContent<TestConfig> component) {
        final SingleUserAuthentication authentication = (SingleUserAuthentication) component.getObject().getAuthentication();
        assertThat(authentication.getSingleUser().getUsername(), is("admin"));
        assertThat(authentication.getSingleUser().getHashedPassword(), is("$2a$12$uGikZXio88E.bl0A3oEe6eR.bAZxfzyifvQ4pAf6uLflCxUA55ONe"));
    }

    @Override
    protected void validateMergedComponent(final ObjectContent<TestConfig> mergedComponent) {
        final SingleUserAuthentication authentication = (SingleUserAuthentication) mergedComponent.getObject().getAuthentication();
        assertThat(authentication.getMethod(), is(LoginTypes.SINGLE_USER));
        assertThat(authentication.getSingleUser().getUsername(), is("admin"));
        assertThat(authentication.getSingleUser().getHashedPassword(), is("$2a$12$uGikZXio88E.bl0A3oEe6eR.bAZxfzyifvQ4pAf6uLflCxUA55ONe"));
        assertThat(authentication.getDefaultLogin().getUsername(), is("admin"));
        assertNotNull(authentication.getDefaultLogin().getPassword());
    }

    @Override
    protected void validateString(final String objectAsString) {
    }
}
