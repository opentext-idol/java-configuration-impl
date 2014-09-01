package com.autonomy.frontend.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/*
 * $Id:$
 *
 * Copyright (c) 2014, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */
@Data
@JsonDeserialize(builder = SingleUserAuthentication.Builder.class)
public class SingleUserAuthentication implements Authentication<SingleUserAuthentication> {

    private final DefaultLogin defaultLogin;
    private final BCryptUsernameAndPassword singleUser;
    private final String method;

    private SingleUserAuthentication(final Builder builder) {
        this.defaultLogin = builder.defaultLogin;
        this.singleUser = builder.singleUser;
        this.method = builder.method;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public UsernameAndPassword getDefaultLogin() {
        return defaultLogin.getDefaultLogin();
    }

    @Override
    public SingleUserAuthentication generateDefaultLogin() {
        final Builder builder = new Builder(this);

        builder.defaultLogin = DefaultLogin.generateDefaultLogin();

        return builder.build();
    }

    @Override
    public SingleUserAuthentication withoutDefaultLogin() {
        final Builder builder = new Builder(this);

        builder.defaultLogin = new DefaultLogin.Builder().build();

        return builder.build();
    }

    @Override
    public SingleUserAuthentication withHashedPasswords() {
        final Builder builder = new Builder(this);

        builder.singleUser = singleUser.withHashedPassword();

        return builder.build();
    }

    @Override
    public SingleUserAuthentication withoutPasswords() {
        final Builder builder = new Builder(this);

        builder.singleUser = singleUser.withoutPasswords();

        return builder.build();
    }

    @Override
    public SingleUserAuthentication merge(final Authentication<?> other) {
        if(other instanceof SingleUserAuthentication) {
            final SingleUserAuthentication castOther = (SingleUserAuthentication) other;

            final Builder builder = new Builder(this);

            builder.setDefaultLogin(this.defaultLogin == null ? castOther.defaultLogin : this.defaultLogin.merge(castOther.defaultLogin));
            builder.setSingleUser(this.singleUser == null ? castOther.singleUser : this.singleUser.merge(castOther.singleUser));
            builder.setMethod(this.method == null ? castOther.method : this.method);

            return builder.build();
        }
        else {
            return this;
        }
    }

    @Override
    public void basicValidate() throws ConfigException {
        if(LoginTypes.SINGLE_USER.equalsIgnoreCase(method)) {
            singleUser.basicValidate();
        }
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public ValidationResult<?> validate(final ConfigService<? extends AuthenticationConfig<?>> configService) {
        final Authentication<?> authentication = configService.getConfig().getAuthentication();

        if(authentication instanceof SingleUserAuthentication) {
            final SingleUserAuthentication current = (SingleUserAuthentication) authentication;
            return singleUser.validate(current.getSingleUser(), current.getDefaultLogin());
        }
        else {
            // TODO: should this be true? e.g. if switching authentication types
            return new ValidationResult<>(false, "Type mismatch: SingleUserAuthentication not found");
        }
    }

    @NoArgsConstructor
    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @JsonIgnoreProperties({"cas", "community"}) // backwards compatibility
    public static class Builder {

        private DefaultLogin defaultLogin = new DefaultLogin.Builder().build();
        private BCryptUsernameAndPassword singleUser;
        private String method;

        public Builder(final SingleUserAuthentication singleUserAuthentication) {
            if(singleUserAuthentication.defaultLogin != null) {
                this.defaultLogin = singleUserAuthentication.defaultLogin;
            }

            this.singleUser = singleUserAuthentication.singleUser;
            this.method = singleUserAuthentication.method;
        }

        public SingleUserAuthentication build() {
            return new SingleUserAuthentication(this);
        }
    }
}
