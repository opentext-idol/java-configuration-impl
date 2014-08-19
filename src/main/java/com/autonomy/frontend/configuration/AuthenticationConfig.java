package com.autonomy.frontend.configuration;

/*
 * $Id:$
 *
 * Copyright (c) 2014, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */
public interface AuthenticationConfig<A extends Authentication<A>, T extends AuthenticationConfig<A,T>> {

    A getAuthentication();

    T withoutDefaultLogin();

    T generateDefaultLogin();

    T withHashedPasswords();

}
