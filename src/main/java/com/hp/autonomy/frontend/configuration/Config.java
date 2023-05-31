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

package com.hp.autonomy.frontend.configuration;

import com.hp.autonomy.frontend.configuration.validation.OptionalConfigurationComponent;
import com.hp.autonomy.frontend.configuration.validation.ValidationService;

import java.util.Map;

/**
 * A basic configuration object.
 * <p>
 * Implementations of this class should be immutable.
 *
 * @param <C> The type of the config.
 */
public interface Config<C extends Config<C>> extends ConfigurationComponent<C> {

    /**
     * Returns a representation of this config as a Map, suitable for use with a {@link ValidationService}.
     *
     * @return A map representation of this config.
     */
    Map<String, OptionalConfigurationComponent<?>> getValidationMap();

    /**
     * Returns a representation of this config as a Map, suitable for use with a {@link ValidationService}.
     *
     * @return A map representation of the enabled components of this config.
     */
    Map<String, OptionalConfigurationComponent<?>> getEnabledValidationMap();

}
