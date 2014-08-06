package com.autonomy.frontend.configuration;

import com.autonomy.aci.client.services.AciService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

/*
 * $Id$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author$ on $Date$
 */

/**
 * Configuration for Login options - how to authenticate and the location of community
 */
@JsonDeserialize(builder = Login.Builder.class)
@Getter
@EqualsAndHashCode
public class Login implements ConfigurationComponent {

    /**
     * The authentication method. This should be either a community security type, or the special values "cas" (use
     * CAS SSO), "external" (assumes an authenticating reverse proxy is used) or "default" (use the credentials
     * in the config file).
     */
    private final String method;

    /**
     * The CAS configuration to use.  This will only apply if method is set to "cas".
     */
    private final CasConfig cas;

    /**
     * The default login.  This will only apply if method is set to "default".
     */
    private final UsernameAndPassword defaultLogin;
    private final ServerConfig community;
    private final BCryptUsernameAndPassword singleUser;

    private Login(final Builder builder) {
        this.method = builder.method;
        this.cas = builder.cas;
        this.defaultLogin = builder.defaultLogin;
        this.community = builder.community;
        this.singleUser = builder.singleUser;
    }

    public Login merge(final Login login)  {
        if(login != null) {
            final Builder builder = new Builder();

            builder.setMethod(this.method == null ? login.method: this.method);
            builder.setCas(this.cas == null ? login.cas : this.cas.merge(login.cas));
            builder.setDefaultLogin(this.defaultLogin == null ? login.defaultLogin : this.defaultLogin.merge(login.defaultLogin));
            builder.setCommunity(this.community == null ? login.community : this.community.merge(login.community));
            builder.setSingleUser(this.singleUser == null ? login.singleUser : this.singleUser.merge(login.singleUser));

            return builder.build();
        }
        else {
            return this;
        }
    }

    /**
     * @return A new Login without a default login.
     */
    public Login withoutDefaultLogin() {
        final Builder builder = new Builder(this);

        builder.defaultLogin = null;

        return builder.build();
    }

    /**
     * @return A new Login with a default username and password
     */
    public Login generateDefaultLogin() {
        final Builder builder = new Builder(this);

        builder.defaultLogin = new UsernameAndPassword("admin", generatePassword());

        return builder.build();
    }

    public Login withHashedPasswords() {
        final Builder builder = new Builder(this);

        if(singleUser != null) {
            builder.singleUser = singleUser.withHashedPassword();
        }

        return builder.build();
    }

    public Login withoutPasswords() {
        final Builder builder = new Builder(this);

        if(singleUser != null) {
            builder.singleUser = singleUser.withoutPasswords();
        }

        return builder.build();
    }

    private String generatePassword() {
        return RandomStringUtils.random(12, true, true);
    }

    public ValidationResult<?> validate(final AciService aciService, final ConfigService<? extends LoginConfig<?>> configService) {
        if(method.equalsIgnoreCase(LoginTypes.SINGLE_USER)) {
            return singleUser.validate(configService);
        }

        return community.validate(aciService, null);
    }

    public void basicValidate() throws ConfigException {
        if(this.method.equalsIgnoreCase(LoginTypes.CAS)){
            this.validateCAS();
        }
        else if(this.method.equalsIgnoreCase(LoginTypes.SINGLE_USER)) {
            this.singleUser.basicValidate();
        }
        else if(!this.method.equalsIgnoreCase(LoginTypes.DEFAULT)){
            this.community.basicValidate("Community");
        }
    }

    private void validateCAS() throws ConfigException {
        if(this.cas == null ||  StringUtils.isBlank(this.cas.getCasServerLoginUrl()) ||
            StringUtils.isBlank(this.cas.getCasServerUrlPrefix()) || StringUtils.isBlank(this.cas.getServerName())){
            throw new ConfigException("Login",
                "CAS attributes have not been defined in the config file. Please specify them in the config file");
        }
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {

        private String method;
        private CasConfig cas;
        private UsernameAndPassword defaultLogin;
        private ServerConfig community;
        private BCryptUsernameAndPassword singleUser;

        public Builder() {}

        public Builder(final Login login) {
            this.method = login.method;
            this.cas = login.cas;
            this.defaultLogin = login.defaultLogin;
            this.community = login.community;
            this.singleUser = login.singleUser;
        }

        public Builder setCas(final CasConfig casConfig) {
            this.cas = casConfig;
            return this;
        }

        public Builder setCommunity(final ServerConfig community) {
            this.community = community;
            return this;
        }

        public Builder setDefaultLogin(final UsernameAndPassword defaultLogin) {
            this.defaultLogin = defaultLogin;
            return this;
        }

        public Builder setMethod(final String method) {
            this.method = method;
            return this;
        }

        public Builder setSingleUser(final BCryptUsernameAndPassword singleUser) {
            this.singleUser = singleUser;
            return this;
        }

        public Login build() {
            return new Login(this);
        }
    }
}
