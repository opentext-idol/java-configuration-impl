/*
 * Copyright 2013-2014 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

/**
 * A {@link ConfigurationComponent} which can be validated without external dependencies
 */
public interface ValidatingConfigurationComponent extends ConfigurationComponent {

    /**
     * @return A {@link ValidationResult} which is valid if the component is valid
     */
    ValidationResult<?> validate();

}
