package com.autonomy.frontend.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.jasypt.util.text.TextEncryptor;
import org.mindrot.jbcrypt.BCrypt;

/*
 * $Id:$
 *
 * Copyright (c) 2014, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */
@Getter
@EqualsAndHashCode
@JsonDeserialize(builder = BCryptUsernameAndPassword.Builder.class)
public class BCryptUsernameAndPassword implements ConfigurationComponent, PasswordsConfig<BCryptUsernameAndPassword> {

    private static final int BCRYPT_HASHING_ROUNDS = 10;

    private final String username;
    private final String currentPassword;
    private final String plaintextPassword;
    private final String hashedPassword;

    private BCryptUsernameAndPassword(final Builder builder) {
        this.username = builder.username;
        this.currentPassword = builder.currentPassword;
        this.plaintextPassword = builder.plaintextPassword;
        this.hashedPassword = builder.hashedPassword;
    }

    public BCryptUsernameAndPassword merge(final BCryptUsernameAndPassword usernameAndPassword) {
        if(usernameAndPassword != null) {
            final Builder builder = new Builder();

            builder.setUsername(username == null ? usernameAndPassword.username : username);
            builder.setHashedPassword(hashedPassword == null ? usernameAndPassword.hashedPassword : this.hashedPassword);
            builder.setCurrentPassword(currentPassword == null ? usernameAndPassword.currentPassword : this.currentPassword);
            builder.setPlaintextPassword(plaintextPassword == null ? usernameAndPassword.plaintextPassword : this.plaintextPassword);

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

    public BCryptUsernameAndPassword withHashedPassword() {
        final Builder builder = new Builder(this);

        builder.setPlaintextPassword(null);

        if(StringUtils.isNotBlank(plaintextPassword)) {
            builder.setHashedPassword(BCrypt.hashpw(plaintextPassword, BCrypt.gensalt(BCRYPT_HASHING_ROUNDS)));
        }
        else {
            builder.setHashedPassword("");
        }

        return builder.build();
    }

    public boolean basicValidate() {
        return username != null;
    }

    public ValidationResult<Void> validate(final ConfigService<? extends LoginConfig<?>> configService) {
        final Login login = configService.getConfig().getLogin();

        final BCryptUsernameAndPassword existingSingleUser = login.getSingleUser();
        final UsernameAndPassword defaultLogin = login.getDefaultLogin();

        final boolean valid;

        if(defaultLogin != null) {
            valid = currentPassword.equals(defaultLogin.getPassword());
        }
        else if(existingSingleUser == null || StringUtils.isEmpty(existingSingleUser.getHashedPassword())) {
            valid = true;
        }
        else {
            valid = BCrypt.checkpw(currentPassword, existingSingleUser.getHashedPassword());
        }

        return new ValidationResult<>(valid);
    }

    @Override
    public BCryptUsernameAndPassword withoutPasswords() {
        final Builder builder = new Builder(this);

        builder.plaintextPassword = "";
        builder.hashedPassword = "";

        return builder.build();
    }

    @Override
    public BCryptUsernameAndPassword withEncryptedPasswords(final TextEncryptor encryptor) {
        return this;
    }

    @Override
    public BCryptUsernameAndPassword withDecryptedPasswords(final TextEncryptor encryptor) {
        return this;
    }

    @JsonPOJOBuilder(withPrefix = "set")
    @Setter
    public static class Builder {
        private String username;
        private String currentPassword = "";
        private String plaintextPassword;
        private String hashedPassword;

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
