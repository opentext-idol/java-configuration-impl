/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.validation;

/**
 * A {@link OptionalConfigurationComponent} which can be validated without external dependencies
 */
public interface ValidatingConfigurationComponent<C extends ValidatingConfigurationComponent<C>> extends OptionalConfigurationComponent<C> {

    /**
     * @return A {@link ValidationResult} which is valid if the component is valid
     */
    ValidationResult<?> validate();

}
