/*
 * (c) Copyright 2013-2015 Micro Focus or one of its affiliates.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Micro Focus and its affiliates
 * and licensors ("Micro Focus") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Micro Focus shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.frontend.configuration;

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
