/*
 * Copyright 2013-2014 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

import org.jasypt.util.text.TextEncryptor;

/**
 * A Config which has some passwords which need encrypting and decrypting.
 *
 * @param <T> The type of the config.
 */
public interface PasswordsConfig<T> {

    T withoutPasswords();

    T withEncryptedPasswords(TextEncryptor encryptor);

    T withDecryptedPasswords(TextEncryptor encryptor);

}
