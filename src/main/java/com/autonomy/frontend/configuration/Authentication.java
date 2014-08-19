package com.autonomy.frontend.configuration;

/*
 * $Id:$
 *
 * Copyright (c) 2014, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */
public interface Authentication<T extends Authentication<T>> extends ConfigurationComponent {

    String getMethod();

    UsernameAndPassword getDefaultLogin();

    /**
     * @return The fully qualified name of the class for use with {@link com.fasterxml.jackson.annotation.JsonTypeInfo}
     */
    String getClassName();

    T generateDefaultLogin();

    T withoutDefaultLogin();

    T withHashedPasswords();

    T withoutPasswords();

    T merge(T other);

    void basicValidate() throws ConfigException;
}
