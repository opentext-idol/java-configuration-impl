package com.hp.autonomy.frontend.configuration;

/*
 * $Id:$
 *
 * Copyright (c) 2014, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */
public interface AuthenticationConfig<T extends AuthenticationConfig<T>> extends AuthenticatingConfig<T> {

    Authentication<?> getAuthentication();

}
