/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
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
