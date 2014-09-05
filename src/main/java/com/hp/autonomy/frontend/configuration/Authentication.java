/*
 * Copyright 2013-2014 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "name")
@JsonSubTypes({
    @JsonSubTypes.Type(SingleUserAuthentication.class),
    @JsonSubTypes.Type(CasAuthentication.class)
})
@JsonIgnoreProperties("className")
public interface Authentication<T extends Authentication<T>> extends ConfigurationComponent {

    String getMethod();

    UsernameAndPassword getDefaultLogin();

    T generateDefaultLogin();

    T withoutDefaultLogin();

    T withHashedPasswords();

    T withoutPasswords();

    T merge(Authentication<?> other);

    void basicValidate() throws ConfigException;
}
