/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.authentication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.LoginTypes;
import com.hp.autonomy.frontend.configuration.validation.ValidationResult;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * {@link Authentication} object for a single username and password
 */
@SuppressWarnings({"InstanceofConcreteClass", "WeakerAccess", "InstanceVariableOfConcreteClass"})
@Data
@JsonDeserialize(builder = SingleUserAuthentication.Builder.class)
@JsonTypeName("SingleUserAuthentication")
public class SingleUserAuthentication implements Authentication<SingleUserAuthentication> {

    private final DefaultLogin defaultLogin;
    private final BCryptUsernameAndPassword singleUser;
    private final String method;

    private SingleUserAuthentication(final Builder builder) {
        defaultLogin = builder.defaultLogin;
        singleUser = builder.singleUser;
        method = builder.method;
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

    @SuppressWarnings({"InstanceofConcreteClass", "CastToConcreteClass"})
    @Override
    public SingleUserAuthentication merge(final Authentication<?> other) {
        return other instanceof SingleUserAuthentication ? merge((SingleUserAuthentication) other) : this;
    }

    @Override
    public SingleUserAuthentication merge(final SingleUserAuthentication other) {
        final Builder builder = new Builder(this);

        builder.setDefaultLogin(defaultLogin == null ? other.defaultLogin : defaultLogin.merge(other.defaultLogin));
        builder.setSingleUser(singleUser == null ? other.singleUser : singleUser.merge(other.singleUser));
        builder.setMethod(method == null ? other.method : method);

        return builder.build();
    }

    @Override
    public void basicValidate(final String section) throws ConfigException {
        if (LoginTypes.SINGLE_USER.equalsIgnoreCase(method)) {
            singleUser.basicValidate(section);
        }
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @SuppressWarnings("CastToConcreteClass")
    public ValidationResult<?> validate(final ConfigService<? extends AuthenticationConfig<?>> configService) {
        final Authentication<?> authentication = configService.getConfig().getAuthentication();

        if (authentication instanceof SingleUserAuthentication) {
            final SingleUserAuthentication current = (SingleUserAuthentication) authentication;
            return singleUser.validate(current.singleUser, current.getDefaultLogin());
        } else {
            // TODO: should this be true? e.g. if switching authentication types
            return new ValidationResult<>(false, "Type mismatch: SingleUserAuthentication not found");
        }
    }

    @SuppressWarnings("InstanceVariableOfConcreteClass")
    @NoArgsConstructor
    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    @JsonIgnoreProperties({"cas", "community", "className"}) // backwards compatibility
    public static class Builder {

        private DefaultLogin defaultLogin = new DefaultLogin.Builder().build();
        private BCryptUsernameAndPassword singleUser;
        private String method;

        public Builder(final SingleUserAuthentication singleUserAuthentication) {
            if (singleUserAuthentication.defaultLogin != null) {
                defaultLogin = singleUserAuthentication.defaultLogin;
            }

            singleUser = singleUserAuthentication.singleUser;
            method = singleUserAuthentication.method;
        }

        public SingleUserAuthentication build() {
            return new SingleUserAuthentication(this);
        }
    }
}
