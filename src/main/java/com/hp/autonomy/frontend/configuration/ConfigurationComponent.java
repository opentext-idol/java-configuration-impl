/*
 * (c) Copyright 2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
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
