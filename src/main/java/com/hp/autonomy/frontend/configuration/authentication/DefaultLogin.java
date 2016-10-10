/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.authentication;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.RandomStringUtils;

/**
 * Wrapper type around a {@link UsernameAndPassword} which allows for generating default password
 */
@Data
@JsonDeserialize(builder = DefaultLogin.Builder.class)
public class DefaultLogin {

    private final UsernameAndPassword defaultLogin;

    private DefaultLogin(final Builder builder) {
        this.defaultLogin = builder.defaultLogin;
    }

    public DefaultLogin merge(final DefaultLogin other) {
        final Builder builder = new Builder(this);

        if (other != null) {
            builder.setDefaultLogin(this.defaultLogin == null ? other.defaultLogin : this.defaultLogin.merge(other.defaultLogin));
        }

        return builder.build();
    }

    /**
     * Static factory for generating pre-populated default logins
     *
     * @return A DefaultLogin with the username "admin" and a random password
     */
    public static DefaultLogin generateDefaultLogin() {
        final Builder builder = new Builder();

        builder.username = "admin";
        builder.password = generatePassword();

        return builder.build();
    }

    private static String generatePassword() {
        return RandomStringUtils.random(12, true, true);
    }

    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {

        private String username;
        private String password;

        private UsernameAndPassword defaultLogin;

        public Builder(final DefaultLogin authentication) {
            username = authentication.defaultLogin.getUsername();
            password = authentication.defaultLogin.getPassword();
        }

        public DefaultLogin build() {
            defaultLogin = new UsernameAndPassword.Builder()
                    .setUsername(username)
                    .setPassword(password)
                    .build();

            return new DefaultLogin(this);
        }

    }

}
