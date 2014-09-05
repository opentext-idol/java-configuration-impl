/*
 * Copyright 2013-2014 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

public abstract class AbstractUnauthenticatingConfigFileService<T extends Config<T>> extends BaseConfigFileService<T> {
    @Override
    public T generateDefaultLogin(final T config) {
        return config;
    }

    @Override
    public T withoutDefaultLogin(final T config) {
        return config;
    }

    @Override
    public T withHashedPasswords(final T config) {
        return config;
    }
}
