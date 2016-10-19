/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

@SuppressWarnings("WeakerAccess")
public interface ConfigurationComponent<C extends ConfigurationComponent<C>> {
    /**
     * Combine this Config with another of the same type and returns a new Config.
     * <p>
     * The new config will have the same attributes as this config, with missing attributes supplied by other.
     * <p>
     * Sub components of the Config should be merged where possible.
     *
     * @param other The configuration to merge with.
     * @return A new Config which is a combination of this and other
     */
    C merge(C other);

    /**
     * Perform a basic validation of the internals of this Config.  This method should not rely on
     * external services.
     *
     * @param section the section to specify if a config exception is thrown if no section should be specified locally
     * @throws ConfigException If validation fails.
     */
    void basicValidate(String section) throws ConfigException;
}
