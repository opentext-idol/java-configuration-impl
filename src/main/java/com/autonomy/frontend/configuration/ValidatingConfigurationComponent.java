package com.autonomy.frontend.configuration;

/*
 * $Id:$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */

/**
 * A {@link ConfigurationComponent} which can be validated without external dependencies
 */
public interface ValidatingConfigurationComponent extends ConfigurationComponent {

    /**
     * @return A {@link ValidationResult} which is valid if the component is valid
     */
    ValidationResult<?> validate();

}
