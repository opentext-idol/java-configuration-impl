package com.autonomy.frontend.configuration;

/*
 * $Id:$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */

/**
 * A Validator is responsible for validating configuration components which have external dependencies.
 *
 * @param <T> The type of objects this Validator can validate
 */
public interface Validator<T extends ConfigurationComponent> {

    /**
     * Validates the given ConfigurationComponent
     *
     * @param config The ConfigurationComponent to validate
     *
     * @return The result of the validation
     */
    ValidationResult<?> validate(T config);

    /**
     * @return The type of object that this Validator can validate.
     */
    Class<T> getSupportedClass();

}
