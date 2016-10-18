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
import com.hp.autonomy.frontend.configuration.SimpleComponent;
import com.hp.autonomy.frontend.configuration.validation.ValidationResult;
import lombok.Builder;
import lombok.Getter;

/**
 * {@link Authentication} object for a single username and password
 */
@SuppressWarnings({"InstanceofConcreteClass", "WeakerAccess", "InstanceVariableOfConcreteClass"})
@Getter
@Builder(toBuilder = true)
@JsonDeserialize(builder = SingleUserAuthentication.SingleUserAuthenticationBuilder.class)
@JsonTypeName("SingleUserAuthentication")
public class SingleUserAuthentication extends SimpleComponent<SingleUserAuthentication> implements Authentication<SingleUserAuthentication> {
    private final UsernameAndPassword defaultLogin;
    private final BCryptUsernameAndPassword singleUser;
    private final String method;

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public SingleUserAuthentication generateDefaultLogin() {
        return toBuilder()
                .defaultLogin(DefaultLogin.generateDefaultLogin())
                .build();
    }

    @Override
    public SingleUserAuthentication withoutDefaultLogin() {
        return toBuilder()
                .defaultLogin(UsernameAndPassword.builder().build())
                .build();
    }

    @Override
    public SingleUserAuthentication withHashedPasswords() {
        return toBuilder()
                .singleUser(singleUser.withHashedPassword())
                .build();
    }

    @Override
    public SingleUserAuthentication withoutPasswords() {
        return toBuilder()
                .singleUser(singleUser.withoutPasswords())
                .build();
    }

    @SuppressWarnings({"InstanceofConcreteClass", "CastToConcreteClass"})
    @Override
    public SingleUserAuthentication merge(final Authentication<?> other) {
        return other instanceof SingleUserAuthentication ? merge((SingleUserAuthentication) other) : this;
    }

    @Override
    public void basicValidate(final String section) throws ConfigException {
        if (LoginTypes.SINGLE_USER.equalsIgnoreCase(method)) {
            singleUser.basicValidate(section);
        }
    }

    @Override
    @JsonIgnore
    public Boolean getEnabled() {
        return true;
    }

    @SuppressWarnings("CastToConcreteClass")
    public ValidationResult<?> validate(final ConfigService<? extends AuthenticationConfig<?>> configService) {
        final Authentication<?> authentication = configService.getConfig().getAuthentication();

        if (authentication instanceof SingleUserAuthentication) {
            final SingleUserAuthentication current = (SingleUserAuthentication) authentication;
            return singleUser.validate(current.singleUser, current.defaultLogin);
        } else {
            // TODO: should this be true? e.g. if switching authentication types
            return new ValidationResult<>(false, "Type mismatch: SingleUserAuthentication not found");
        }
    }

    @SuppressWarnings("InstanceVariableOfConcreteClass")
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties({"cas", "community", "className"}) // backwards compatibility
    public static class SingleUserAuthenticationBuilder {
    }
}
