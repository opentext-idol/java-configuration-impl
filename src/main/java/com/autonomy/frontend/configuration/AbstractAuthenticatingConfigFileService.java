package com.autonomy.frontend.configuration;

import lombok.extern.slf4j.Slf4j;

/*
 * $Id$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author$ on $Date$
 */

/**
 * Reference implementation of {@link com.autonomy.frontend.configuration.ConfigFileService}, which outputs configuration objects as JSON files.
 * An additional type bound is placed on the configuration object this class uses.
 *
 * This class requires that a default config file be available at runtime.
 *
 * Operations on the Config are thread safe.
 *
 * @param <T> The type of the Configuration object. If it extends {@link com.autonomy.frontend.configuration.PasswordsConfig}, passwords will be encrypted
 *           and decrypted when the file is written and read respectively.  If it extends {@link com.autonomy.frontend.configuration.LoginConfig}, a default
 *           login will be generated for the initial config file, and which will be removed on subsequent writes.
 *
 */
@Slf4j
public abstract class AbstractAuthenticatingConfigFileService<T extends Config<T> & AuthenticatingConfig<T>> extends BaseConfigFileService<T> {

    @Override
    T withHashedPasswords(final T config) {
        if(config != null) {
            return config.withHashedPasswords();
        }

        return config;
    }

    @Override
    T withoutDefaultLogin(final T config) {
        if(config != null) {
            return config.withoutDefaultLogin();
        }

        return config;
    }

    @Override
    T generateDefaultLogin(final T config) {
        if(config != null) {
            return config.generateDefaultLogin();
        }

        return config;
    }
}
