package com.autonomy.frontend.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.RandomStringUtils;

/*
 * $Id:$
 *
 * Copyright (c) 2014, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
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

        if(other != null) {
            builder.setDefaultLogin(this.defaultLogin == null ? other.defaultLogin : this.defaultLogin.merge(other.defaultLogin));
        }

        return builder.build();
    }

    public DefaultLogin generateDefaultLogin() {
        final Builder builder = new Builder(this);

        builder.username = "admin";
        builder.password = generatePassword();

        return builder.build();
    }

    private String generatePassword() {
        return RandomStringUtils.random(12, true, true);
    }

    @Setter
    @Accessors(chain = true)
    @NoArgsConstructor
    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder{

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
