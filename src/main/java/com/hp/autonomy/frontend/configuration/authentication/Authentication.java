/*
 * Copyright 2013-2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.frontend.configuration.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.validation.OptionalConfigurationComponent;

/**
 * Interface representing the configuration for an authentication method
 *
 * @param <T> The type of this authentication object
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "name")
@JsonSubTypes({
        @JsonSubTypes.Type(SingleUserAuthentication.class),
        @JsonSubTypes.Type(CasAuthentication.class)
})
@JsonIgnoreProperties("className")
public interface Authentication<T extends Authentication<T>> extends OptionalConfigurationComponent<T> {

    /**
     * @return The authentication method to use
     */
    String getMethod();

    /**
     * @return The credentials for the default login
     */
    UsernameAndPassword getDefaultLogin();

    /**
     * @return A copy of this object with a default login, or this if a default login is not required
     */
    T generateDefaultLogin();

    /**
     * @return A copy of this object without a default login, or this if a default login is not required
     */
    T withoutDefaultLogin();

    /**
     * @return A copy of this object with hashed passwords, or this if there are no passwords
     */
    T withHashedPasswords();

    /**
     * @return A copy of this object without passwords or password hashes, or this if there are no passwords
     */
    T withoutPasswords();

    /**
     * Merges this Authentication with another, by calling the merge method of all its fields.
     * For types which do not have a merge method, use values from other only when the values on this are null
     *
     * @param other The other authentication object
     * @return A combination of this object and other
     */
    T merge(Authentication<?> other);
}
