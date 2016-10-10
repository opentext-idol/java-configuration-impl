/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.passwords;

import org.jasypt.util.text.TextEncryptor;

/**
 * A Config which has some passwords which need encrypting and decrypting.
 *
 * @param <T> The type of the config.
 */
public interface PasswordsConfig<T> {

    /**
     * @return A copy of this config with no passwords, or this if the config has no passwords
     */
    T withoutPasswords();

    /**
     * @return A copy of this config with passwords encrypted by the supplied {@link TextEncryptor},
     * or this if the config has no passwords
     */
    T withEncryptedPasswords(TextEncryptor encryptor);

    /**
     * @return A copy of this config with passwords decrypted by the supplied {@link TextEncryptor},
     * or this if the config has no passwords
     */
    T withDecryptedPasswords(TextEncryptor encryptor);

}
