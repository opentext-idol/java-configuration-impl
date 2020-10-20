/*
 * (c) Copyright 2013-2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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
     * @param encryptor object responsible for password encryption
     * @return A copy of this config with passwords encrypted by the supplied {@link TextEncryptor},
     * or this if the config has no passwords
     */
    T withEncryptedPasswords(TextEncryptor encryptor);

    /**
     * @param encryptor object responsible for password encryption
     * @return A copy of this config with passwords decrypted by the supplied {@link TextEncryptor},
     * or this if the config has no passwords
     */
    T withDecryptedPasswords(TextEncryptor encryptor);

}
