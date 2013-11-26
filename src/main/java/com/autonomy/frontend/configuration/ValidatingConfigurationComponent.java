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
     * @return True if the component is valid; false otherwise
     */
    boolean validate();

}
