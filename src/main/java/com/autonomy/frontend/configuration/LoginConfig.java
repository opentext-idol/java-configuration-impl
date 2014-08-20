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
 * @deprecated Use {@link AuthenticationConfig} instead
 */
@Deprecated
public interface LoginConfig<T> extends AuthenticatingConfig<T> {

    Login getLogin();

}
