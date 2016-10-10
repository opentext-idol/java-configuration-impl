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
import com.hp.autonomy.frontend.configuration.validation.ValidatingConfigurationComponent;
import com.hp.autonomy.frontend.configuration.validation.ValidationResult;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * {@link Authentication} for using CAS
 */
@SuppressWarnings({"WeakerAccess", "InstanceVariableOfConcreteClass"})
@Data
@JsonDeserialize(builder = CasAuthentication.Builder.class)
@JsonTypeName("CasAuthentication")
public class CasAuthentication implements Authentication<CasAuthentication>, ValidatingConfigurationComponent<CasAuthentication> {

    private final DefaultLogin defaultLogin;
    private final String method;
    private final CasConfig cas;

    private CasAuthentication(final Builder builder) {
        cas = builder.cas;
        defaultLogin = builder.defaultLogin;
        method = builder.method;
    }

    @SuppressWarnings({"InstanceofConcreteClass", "CastToConcreteClass"})
    @Override
    public CasAuthentication merge(final Authentication<?> other) {
        return other instanceof CasAuthentication ? merge((CasAuthentication) other) : this;
    }

    @Override
    public CasAuthentication merge(final CasAuthentication other) {
        final Builder builder = new Builder(this);

        builder.setDefaultLogin(defaultLogin == null ? other.defaultLogin : defaultLogin.merge(other.defaultLogin));
        builder.setCas(cas == null ? other.cas : cas.merge(other.cas));
        builder.setMethod(method == null ? other.method : method);

        return builder.build();
    }

    @Override
    public void basicValidate(final String section) throws ConfigException {
        if (LoginTypes.CAS.equalsIgnoreCase(method)) {
            cas.basicValidate();
        }
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
    public ValidationResult<?> validate() {
        try {
            cas.basicValidate();

            return new ValidationResult<Void>(true);
        } catch (final ConfigException e) {
            return new ValidationResult<>(false, e.getMessage());
        }
    }

    @Override
    public CasAuthentication generateDefaultLogin() {
        final Builder builder = new Builder(this);

        builder.defaultLogin = DefaultLogin.generateDefaultLogin();

        return builder.build();
    }

    @Override
    public CasAuthentication withoutDefaultLogin() {
        final Builder builder = new Builder(this);

        builder.defaultLogin = new DefaultLogin.Builder().build();

        return builder.build();
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
    public boolean isEnabled() {
        return true;
    }

    @NoArgsConstructor
    @JsonPOJOBuilder(withPrefix = "set")
    @Accessors(chain = true)
    @Setter
    @JsonIgnoreProperties({"singleUser", "community", "className"}) // backwards compatibility
    public static class Builder {

        private CasConfig cas;
        private DefaultLogin defaultLogin = new DefaultLogin.Builder().build();
        private String method;

        public Builder(final CasAuthentication casAuthentication) {
            cas = casAuthentication.cas;
            if (casAuthentication.defaultLogin != null) {
                defaultLogin = casAuthentication.defaultLogin;
            }
            method = casAuthentication.method;
        }

        public CasAuthentication build() {
            return new CasAuthentication(this);
        }
    }

}
