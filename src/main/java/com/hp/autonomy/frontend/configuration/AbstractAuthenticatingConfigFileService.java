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

import com.hp.autonomy.frontend.configuration.authentication.AuthenticatingConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link BaseConfigFileService} which will generate a default login when creating new config files
 * and remove the default login and hash passwords when writing.
 *
 * @param <T> The type of the Configuration object. A default login will be generated for the
 *            initial config file, and will be removed on subsequent writes.
 */
@Slf4j
public abstract class AbstractAuthenticatingConfigFileService<T extends Config<T> & AuthenticatingConfig<T>> extends BaseConfigFileService<T> {

    @Override
    public T withHashedPasswords(final T config) {
        if (config != null) {
            return config.withHashedPasswords();
        }

        return config;
    }

    @Override
    public T withoutDefaultLogin(final T config) {
        if (config != null) {
            return config.withoutDefaultLogin();
        }

        return config;
    }

    @Override
    public T generateDefaultLogin(final T config) {
        if (config != null) {
            return config.generateDefaultLogin();
        }

        return config;
    }
}
