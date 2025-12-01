/*
 * Copyright 2013-2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.frontend.configuration.authentication;

import org.apache.commons.lang3.RandomStringUtils;

@SuppressWarnings("UtilityClass")
class DefaultLogin {
    private static final String DEFAULT_ADMIN_USER = "admin";
    private static final int GENERATED_PASSWORD_LENGTH = 12;

    /**
     * Static factory for generating pre-populated default logins
     *
     * @return A DefaultLogin with the username "admin" and a random password
     */
    public static UsernameAndPassword generateDefaultLogin() {
        return UsernameAndPassword.builder()
                .username(DEFAULT_ADMIN_USER)
                .password(generatePassword())
                .build();
    }

    private static String generatePassword() {
        return RandomStringUtils.random(GENERATED_PASSWORD_LENGTH, true, true);
    }
}
