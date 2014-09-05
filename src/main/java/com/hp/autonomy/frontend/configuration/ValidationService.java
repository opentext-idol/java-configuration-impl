/*
 * Copyright 2013-2014 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

/**
 * A service which allows validation of configurations and configuration components
 * @param <T> The type of the config
 */
public interface ValidationService<T extends Config<T>> {

    /**
     * @param config The config to validate
     * @return The results of the validation
     */
    ValidationResults validateConfig(T config);

    /**
     * Validate the enabled sections of a config.
     *
     * @param config The config to be validated
     * @return The results of the validation
     */
    ValidationResults validateEnabledConfig(T config);

    /**
     * Validate a configuration component
     *
     * @param configurationComponent The component to validate
     * @param <E> The type of the configuration component
     * @return true if the component is valid; false otherwise.
     */
    <E extends ConfigurationComponent> ValidationResult<?> validate(final E configurationComponent);

}
