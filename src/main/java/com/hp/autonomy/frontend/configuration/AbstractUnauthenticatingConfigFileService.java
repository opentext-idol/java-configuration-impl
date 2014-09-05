/*
 * Copyright 2013-2014 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

/**
 * Implementation of {@link BaseConfigFileService} for types which do not deal with authentication.  The methods
 * {@link #generateDefaultLogin(Config)}, {@link #withoutDefaultLogin(Config)} and {@link #withHashedPasswords(Config)}
 * are implemented as identity methods
 *
 * @param <T> The type of the Configuration object
 */
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
