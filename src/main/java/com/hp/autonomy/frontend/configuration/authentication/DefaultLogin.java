/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.authentication;

import org.apache.commons.lang.RandomStringUtils;

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
