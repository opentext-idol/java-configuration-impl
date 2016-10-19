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
import com.hp.autonomy.frontend.configuration.LoginTypes;
import com.hp.autonomy.frontend.configuration.SimpleComponent;
import com.hp.autonomy.frontend.configuration.validation.ValidatingConfigurationComponent;
import com.hp.autonomy.frontend.configuration.validation.ValidationResult;
import lombok.Builder;
import lombok.Getter;

/**
 * {@link Authentication} for using CAS
 */
@SuppressWarnings({"WeakerAccess", "InstanceVariableOfConcreteClass"})
@Getter
@Builder(toBuilder = true)
@JsonDeserialize(builder = CasAuthentication.CasAuthenticationBuilder.class)
@JsonTypeName("CasAuthentication")
public class CasAuthentication extends SimpleComponent<CasAuthentication> implements Authentication<CasAuthentication>, ValidatingConfigurationComponent<CasAuthentication> {
    private final UsernameAndPassword defaultLogin;
    private final String method;
    private final CasConfig cas;

    @SuppressWarnings({"InstanceofConcreteClass", "CastToConcreteClass"})
    @Override
    public CasAuthentication merge(final Authentication<?> other) {
        return other instanceof CasAuthentication ? merge((CasAuthentication) other) : this;
    }

    @Override
    public void basicValidate(final String section) throws ConfigException {
        if (LoginTypes.CAS.equalsIgnoreCase(method)) {
            cas.basicValidate(section);
        }
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public UsernameAndPassword getDefaultLogin() {
        return defaultLogin;
    }

    @Override
    public ValidationResult<?> validate() {
        try {
            cas.basicValidate(null);

            return new ValidationResult<Void>(true);
        } catch (final ConfigException e) {
            return new ValidationResult<>(false, e.getMessage());
        }
    }

    @Override
    public CasAuthentication generateDefaultLogin() {
        return toBuilder()
                .defaultLogin(DefaultLogin.generateDefaultLogin())
                .build();
    }

    @Override
    public CasAuthentication withoutDefaultLogin() {
        return toBuilder()
                .defaultLogin(UsernameAndPassword.builder().build())
                .build();
    }

    @Override
    public CasAuthentication withHashedPasswords() {
        return this;
    }

    @Override
    public CasAuthentication withoutPasswords() {
        return this;
    }

    @Override
    @JsonIgnore
    public Boolean getEnabled() {
        return true;
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties({"singleUser", "community", "className"}) // backwards compatibility
    public static class CasAuthenticationBuilder {
    }

}
