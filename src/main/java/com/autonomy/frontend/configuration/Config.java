package com.autonomy.frontend.configuration;

import java.util.Map;

/*
 * $Id:$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */

/**
 * A basic configuration object.
 *
 * Implementations of this class should be immutable.
 *
 * @param <T> The type of the config.
 */
public interface Config<T extends Config<T>> {

    /**
     * Returns a representation of this config as a Map, suitable for use with a {@link ValidationService}.
     *
     * @return A map representation of this config.
     */
    Map<String, ConfigurationComponent> getValidationMap();

    /**
     * Returns a representation of the enabled {@link ConfigurationComponent} this config
     * as a Map, suitable for use with a {@link ValidationService}.
     *
     * @return A map representation of the enabled components of this config.
     */
    Map<String, ConfigurationComponent> getEnabledValidationMap();

    /**
     * Perform a basic validation of the internals of this Config.  This method should not rely on
     * external services.
     *
     * @throws ConfigException If validation fails.
     */
    void basicValidate() throws ConfigException;

    /**
     * Combine this Config with another of the same type and returns a new Config.
     *
     * The new config will have the same attributes as this config, with missing attributes supplied by other.
     *
     * Sub components of the Config should be merged where possible.
     *
     * @param other The configuration to merge with.
     *
     * @return A new Config which is a combination of this and other
     */
    T merge(T other);

}
