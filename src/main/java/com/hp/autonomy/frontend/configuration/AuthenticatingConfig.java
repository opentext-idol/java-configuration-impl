/*
 * Copyright 2013-2014 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

public interface AuthenticatingConfig<T> {

    T withoutDefaultLogin();

    T generateDefaultLogin();

    T withHashedPasswords();

}
