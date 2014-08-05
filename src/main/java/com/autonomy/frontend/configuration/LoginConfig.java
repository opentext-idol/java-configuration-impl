package com.autonomy.frontend.configuration;

/*
 * $Id:$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */

/**
 * Interface representing a config that contains a {@link Login}
 * @param <T> The type of the config.
 */
public interface LoginConfig<T> {

    Login getLogin();

    T withoutDefaultLogin();

    T generateDefaultLogin();

    T withHashedPasswords();

}
