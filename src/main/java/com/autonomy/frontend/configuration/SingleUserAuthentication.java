package com.autonomy.frontend.configuration;

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
    public String getClassName() {
        return getClass().getCanonicalName();
    }

    @Override
    public SingleUserAuthentication generateDefaultLogin() {
        final Builder builder = new Builder(this);

        builder.defaultLogin = defaultLogin.generateDefaultLogin();

        return this;
    }

    @Override
    public SingleUserAuthentication withoutDefaultLogin() {
        final Builder builder = new Builder(this);

        builder.defaultLogin = null;

        return this;
    }

    @Override
    public SingleUserAuthentication withHashedPasswords() {
        final Builder builder = new Builder(this);

        builder.singleUser = singleUser.withHashedPassword();

        return this;
    }

    @Override
    public SingleUserAuthentication withoutPasswords() {
        final Builder builder = new Builder(this);

        builder.singleUser = singleUser.withoutPasswords();

        return this;
    }

    @Override
    public SingleUserAuthentication merge(final SingleUserAuthentication other) {
        if(other != null) {
            final Builder builder = new Builder(this);

            builder.setDefaultLogin(this.defaultLogin == null ? other.defaultLogin : this.defaultLogin.merge(other.defaultLogin));
            builder.setSingleUser(this.singleUser == null ? other.singleUser : this.singleUser.merge(other.singleUser));
            builder.setMethod(this.method == null ? other.method : this.method);

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
    public boolean isEnabled() {
        return true;
    }

    public ValidationResult<?> validate(final ConfigService<? extends AuthenticationConfig<? extends SingleUserAuthentication, ?>> configService) {
        return singleUser.validate(configService);
    }

    @NoArgsConstructor
    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    public static class Builder {

        private DefaultLogin defaultLogin;
        private BCryptUsernameAndPassword singleUser;
        private String method;

        public Builder(final SingleUserAuthentication singleUserAuthentication) {
            this.defaultLogin = singleUserAuthentication.defaultLogin;
            this.singleUser = singleUserAuthentication.singleUser;
            this.method = singleUserAuthentication.method;
        }

        public SingleUserAuthentication build() {
            return new SingleUserAuthentication(this);
        }
    }
}
