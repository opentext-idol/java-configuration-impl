/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.authentication;

/**
 * A configuration object which contains an {@link Authentication} object
 *
 * @param <T> The type of the configuration object
 */
public interface AuthenticationConfig<T extends AuthenticationConfig<T>> extends AuthenticatingConfig<T> {

    Authentication<?> getAuthentication();

}
