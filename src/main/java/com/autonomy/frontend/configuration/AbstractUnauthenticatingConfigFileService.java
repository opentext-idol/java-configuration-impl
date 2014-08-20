package com.autonomy.frontend.configuration;

/*
 * $Id:$
 *
 * Copyright (c) 2014, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */
public abstract class AbstractUnauthenticatingConfigFileService<T extends Config<T>> extends BaseConfigFileService<T> {
    @Override
    T generateDefaultLogin(final T config) {
        return config;
    }

    @Override
    T withoutDefaultLogin(final T config) {
        return config;
    }

    @Override
    T withHashedPasswords(final T config) {
        return config;
    }
}
