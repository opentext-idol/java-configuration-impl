package com.autonomy.frontend.configuration;

/*
 * $Id:$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */

/**
 * A {@link ConfigService} which allows the config to be updated.
 * @param <T>
 */
public interface WriteableConfigService<T> extends ConfigService<T> {

    /**
     * Updates the configuration to the given config.
     *
     * This method should be thread safe.
     *
     * @param config The new config
     * @throws Exception If an error occurs while saving the config
     */
    @SuppressWarnings("ProhibitedExceptionDeclared")
    void updateConfig(final T config) throws Exception;

}
