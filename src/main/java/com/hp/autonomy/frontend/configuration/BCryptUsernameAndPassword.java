/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Configuration object for a username and password where the password is hashed using BCrypt
 */
@Getter
@EqualsAndHashCode
@JsonDeserialize(builder = BCryptUsernameAndPassword.Builder.class)
public class BCryptUsernameAndPassword implements ConfigurationComponent {

    private static final int BCRYPT_LOG_HASHING_ROUNDS = 10;

    private final String username;
    private final String currentPassword;
    private final String plaintextPassword;
    private final String hashedPassword;
    private final boolean passwordRedacted;

    private BCryptUsernameAndPassword(final Builder builder) {
        this.username = builder.username;
        this.currentPassword = builder.currentPassword;
        this.plaintextPassword = builder.plaintextPassword;
        this.hashedPassword = builder.hashedPassword;
        this.passwordRedacted = builder.passwordRedacted;
    }

    public BCryptUsernameAndPassword merge(final BCryptUsernameAndPassword usernameAndPassword) {
        if(usernameAndPassword != null) {
            final Builder builder = new Builder();

            builder.setUsername(username == null ? usernameAndPassword.username : username);
            builder.setHashedPassword(passwordRedacted || hashedPassword == null ? usernameAndPassword.hashedPassword : this.hashedPassword);
            builder.setCurrentPassword(passwordRedacted || currentPassword == null ? usernameAndPassword.currentPassword : this.currentPassword);
            builder.setPlaintextPassword(passwordRedacted || plaintextPassword == null ? usernameAndPassword.plaintextPassword : this.plaintextPassword);
            builder.setPasswordRedacted(false);

            return builder.build();
        }
        else {
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

        if(hashedPassword != null && StringUtils.isNotBlank(plaintextPassword)) {
            builder.setHashedPassword(BCrypt.hashpw(plaintextPassword, BCrypt.gensalt(BCRYPT_LOG_HASHING_ROUNDS)));
        }

        return builder.build();
    }

    public boolean basicValidate() {
        return username != null;
    }

    /**
     * Validates this component by comparing the current password against either a default login or an existing single
     * user
     * @param existingSingleUser The current single user
     * @param defaultLogin The current default credentials.  May be null
     * @return A true {@link ValidationResult} if valid, or false otherwise.  The false result includes a detail message
     */
    public ValidationResult<?> validate(final BCryptUsernameAndPassword existingSingleUser, final UsernameAndPassword defaultLogin) {
        if(passwordRedacted) {
            return new ValidationResult<>(true);
        }

        final boolean valid;

        if(defaultLogin.getPassword() != null) {
            valid = currentPassword.equals(defaultLogin.getPassword());
        }
        else {
            valid = BCrypt.checkpw(currentPassword, existingSingleUser.hashedPassword);
        }

        if(valid) {
            return new ValidationResult<>(true);
        }
        else {
            return new ValidationResult<>(false, "The current password is incorrect");
        }
    }

    public BCryptUsernameAndPassword withoutPasswords() {
        final Builder builder = new Builder(this);

        builder.plaintextPassword = null;

        if(StringUtils.isNotEmpty(builder.hashedPassword)) {
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

        public Builder() {}

        public Builder(final BCryptUsernameAndPassword usernameAndPassword) {
            this.username = usernameAndPassword.username;
            this.currentPassword = usernameAndPassword.currentPassword;
            this.plaintextPassword = usernameAndPassword.plaintextPassword;
            this.hashedPassword = usernameAndPassword.hashedPassword;
        }

        public BCryptUsernameAndPassword build() {
            return new BCryptUsernameAndPassword(this);
        }
    }
}
