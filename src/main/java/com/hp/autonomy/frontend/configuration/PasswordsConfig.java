package com.hp.autonomy.frontend.configuration;

import org.jasypt.util.text.TextEncryptor;

/*
 * $Id:$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */

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
