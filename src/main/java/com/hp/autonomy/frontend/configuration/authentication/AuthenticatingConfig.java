/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.authentication;

/**
 * This interface exists for backwards compatibility reasons.  Prefer {@link AuthenticationConfig} instead.
 *
 * @param <T> The type of the configuration object
 */
public interface AuthenticatingConfig<T> {

    T withoutDefaultLogin();

    T generateDefaultLogin();

    T withHashedPasswords();

}
