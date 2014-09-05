package com.hp.autonomy.frontend.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/*
 * $Id:$
 *
 * Copyright (c) 2014, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */
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
