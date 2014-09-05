package com.hp.autonomy.frontend.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/*
 * $Id$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author$ on $Date$
 */

/**
 * Configuration object representing a username and password combination
 */
@Getter
@EqualsAndHashCode
@JsonDeserialize(builder = UsernameAndPassword.Builder.class)
public class UsernameAndPassword {

    private final String username;
    private final String password;

    public UsernameAndPassword(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    private UsernameAndPassword(final Builder builder) {
        this.username = builder.username;
        this.password = builder.password;
    }

    public UsernameAndPassword merge(final UsernameAndPassword usernameAndPassword) {
        if(usernameAndPassword != null) {
            final Builder builder = new Builder();

            builder.setUsername(username == null ? usernameAndPassword.username : this.username);
            builder.setUsername(password == null ? usernameAndPassword.password : this.password);

            return builder.build();
        }
        else {
            return this;
        }
    }

    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {
        private String username;
        private String password;

        public Builder setPassword(final String password) {
            this.password = password;
            return this;
        }

        public Builder setUsername(final String username) {
            this.username = username;
            return this;
        }

        public UsernameAndPassword build() {
            return new UsernameAndPassword(this);
        }
    }

}
