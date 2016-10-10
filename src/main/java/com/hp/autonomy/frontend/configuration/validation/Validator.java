/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.validation;

/**
 * A Validator is responsible for validating configuration components which have external dependencies.
 *
 * @param <T> The type of objects this Validator can validate
 */
public interface Validator<T extends OptionalConfigurationComponent<T>> {

    /**
     * Validates the given OptionalConfigurationComponent
     *
     * @param config The OptionalConfigurationComponent to validate
     * @return The result of the validation
     */
    ValidationResult<?> validate(T config);

    /**
     * @return The type of object that this Validator can validate.
     */
    Class<T> getSupportedClass();

}
