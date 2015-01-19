/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
public interface Authentication<T extends Authentication<T>> extends ConfigurationComponent {

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
     * @param other The other authentication object
     * @return A combination of this object and other
     */
    T merge(Authentication<?> other);

    void basicValidate() throws ConfigException;
}
