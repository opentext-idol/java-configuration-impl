/*
 * Copyright 2013-2014 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

import com.hp.autonomy.frontend.configuration.WriteableConfigService;

/**
 * A {@link WriteableConfigService} which writes its configuration to a file.
 *
 * @param <T> The type of the Config
 */
public interface ConfigFileService<T> extends WriteableConfigService<T> {

    /**
     * Returns a {@link ConfigResponse} which contains the config, along with the path to the file and the
     * environment variable used to set the path.
     *
     * @return A ConfigResponse containing the Config and associated metadata
     */
    ConfigResponse<T> getConfigResponse();

}
