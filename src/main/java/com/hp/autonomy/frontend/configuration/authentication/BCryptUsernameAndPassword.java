/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.authentication;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.validation.OptionalConfigurationComponent;
import com.hp.autonomy.frontend.configuration.validation.ValidationResult;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Configuration object for a username and password where the password is hashed using BCrypt
 */
@SuppressWarnings("WeakerAccess")
@Getter
@EqualsAndHashCode
@JsonDeserialize(builder = BCryptUsernameAndPassword.Builder.class)
public class BCryptUsernameAndPassword implements OptionalConfigurationComponent<BCryptUsernameAndPassword> {

    private static final int BCRYPT_LOG_HASHING_ROUNDS = 10;

    private final String username;
    private final String currentPassword;
    private final String plaintextPassword;
    private final String hashedPassword;
    private final boolean passwordRedacted;

    private BCryptUsernameAndPassword(final Builder builder) {
        username = builder.username;
        currentPassword = builder.currentPassword;
        plaintextPassword = builder.plaintextPassword;
        hashedPassword = builder.hashedPassword;
        passwordRedacted = builder.passwordRedacted;
    }

    @Override
    public BCryptUsernameAndPassword merge(final BCryptUsernameAndPassword usernameAndPassword) {
        if (usernameAndPassword != null) {
            final Builder builder = new Builder();

            builder.setUsername(username == null ? usernameAndPassword.username : username);
            builder.setHashedPassword(passwordRedacted || hashedPassword == null ? usernameAndPassword.hashedPassword : hashedPassword);
            builder.setCurrentPassword(passwordRedacted || currentPassword == null ? usernameAndPassword.currentPassword : currentPassword);
            builder.setPlaintextPassword(passwordRedacted || plaintextPassword == null ? usernameAndPassword.plaintextPassword : plaintextPassword);
            builder.setPasswordRedacted(false);

            return builder.build();
        } else {
            return this;
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * @return A copy of this object with a hashed password and no plaintext password
     */
    public BCryptUsernameAndPassword withHashedPassword() {
        final Builder builder = new Builder(this);

        builder.setPlaintextPassword(null);
        builder.setCurrentPassword(null);

        if (hashedPassword != null && StringUtils.isNotBlank(plaintextPassword)) {
            builder.setHashedPassword(BCrypt.hashpw(plaintextPassword, BCrypt.gensalt(BCRYPT_LOG_HASHING_ROUNDS)));
        }

        return builder.build();
    }

    @Override
    public void basicValidate(final String section) throws ConfigException {
        if (username == null) {
            throw new ConfigException(section, "No username specified");
        }
    }

    /**
     * Validates this component by comparing the current password against either a default login or an existing single
     * user
     *
     * @param existingSingleUser The current single user
     * @param defaultLogin       The current default credentials.  May be null
     * @return A true {@link ValidationResult} if valid, or false otherwise.  The false result includes a detail message
     */
    public ValidationResult<?> validate(final BCryptUsernameAndPassword existingSingleUser, final UsernameAndPassword defaultLogin) {
        if (passwordRedacted) {
            return new ValidationResult<>(true);
        }

        final boolean valid = defaultLogin.getPassword() != null ? currentPassword.equals(defaultLogin.getPassword()) : BCrypt.checkpw(currentPassword, existingSingleUser.hashedPassword);
        return valid ? new ValidationResult<>(true) : new ValidationResult<>(false, "The current password is incorrect");
    }

    public BCryptUsernameAndPassword withoutPasswords() {
        final Builder builder = new Builder(this);

        builder.plaintextPassword = null;

        if (StringUtils.isNotEmpty(builder.hashedPassword)) {
            builder.hashedPassword = null;
            builder.passwordRedacted = true;
        }

        return builder.build();
    }

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    @Accessors(chain = true)
    public static class Builder {
        private String username;
        private String currentPassword;
        private String plaintextPassword;
        private String hashedPassword;
        private boolean passwordRedacted;

        public Builder() {
        }

        public Builder(final BCryptUsernameAndPassword usernameAndPassword) {
            username = usernameAndPassword.username;
            currentPassword = usernameAndPassword.currentPassword;
            plaintextPassword = usernameAndPassword.plaintextPassword;
            hashedPassword = usernameAndPassword.hashedPassword;
        }

        public BCryptUsernameAndPassword build() {
            return new BCryptUsernameAndPassword(this);
        }
    }
}
