/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.authentication;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.SimpleComponent;
import com.hp.autonomy.frontend.configuration.validation.OptionalConfigurationComponent;
import com.hp.autonomy.frontend.configuration.validation.ValidationResult;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Configuration object for a username and password where the password is hashed using BCrypt
 */
@SuppressWarnings({"WeakerAccess", "DefaultAnnotationParam"})
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@JsonDeserialize(builder = BCryptUsernameAndPassword.BCryptUsernameAndPasswordBuilder.class)
public class BCryptUsernameAndPassword extends SimpleComponent<BCryptUsernameAndPassword> implements OptionalConfigurationComponent<BCryptUsernameAndPassword> {

    private static final int BCRYPT_LOG_HASHING_ROUNDS = 10;

    private final String username;
    private final String currentPassword;
    private final String plaintextPassword;
    private final String hashedPassword;
    private final boolean passwordRedacted;

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public BCryptUsernameAndPassword merge(final BCryptUsernameAndPassword other) {
        return super.merge(other).toBuilder()
                .passwordRedacted(false)
                .build();
    }

    /**
     * @return A copy of this object with a hashed password and no plaintext password
     */
    public BCryptUsernameAndPassword withHashedPassword() {
        final BCryptUsernameAndPasswordBuilder builder = toBuilder()
                .plaintextPassword(null)
                .currentPassword(null);

        if (hashedPassword != null && StringUtils.isNotBlank(plaintextPassword)) {
            builder.hashedPassword(BCrypt.hashpw(plaintextPassword, BCrypt.gensalt(BCRYPT_LOG_HASHING_ROUNDS)));
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
        final BCryptUsernameAndPasswordBuilder builder = toBuilder()
                .plaintextPassword(null);

        if (StringUtils.isNotEmpty(builder.hashedPassword)) {
            builder.hashedPassword(null);
            builder.passwordRedacted(true);
        }

        return builder.build();
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class BCryptUsernameAndPasswordBuilder {
    }
}
