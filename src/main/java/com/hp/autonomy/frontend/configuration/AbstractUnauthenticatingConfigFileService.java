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
