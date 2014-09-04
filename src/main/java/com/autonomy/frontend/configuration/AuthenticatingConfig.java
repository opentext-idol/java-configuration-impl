package com.autonomy.frontend.configuration;

/*
 * $Id:$
 *
 * Copyright (c) 2014, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */
public interface AuthenticatingConfig<T> {

    T withoutDefaultLogin();

    T generateDefaultLogin();

    T withHashedPasswords();

}
