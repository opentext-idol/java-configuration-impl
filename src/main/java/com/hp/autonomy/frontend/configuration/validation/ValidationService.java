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

package com.hp.autonomy.frontend.configuration.validation;

import com.hp.autonomy.frontend.configuration.Config;

/**
 * A service which allows validation of configurations and configuration components
 *
 * @param <C> The type of the config
 */
public interface ValidationService<C extends Config<C>> {

    /**
     * @param config The config to validate
     * @return The results of the validation
     */
    ValidationResults validateConfig(C config);

    /**
     * Validate the enabled sections of a config.
     *
     * @param config The config to be validated
     * @return The results of the validation
     */
    ValidationResults validateEnabledConfig(C config);

    /**
     * Validate a configuration component
     *
     * @param configurationComponent The component to validate
     * @return true if the component is valid; false otherwise.
     */
    ValidationResult<?> validate(final OptionalConfigurationComponent<?> configurationComponent);

}
