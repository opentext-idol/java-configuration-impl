/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.server;

import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.SimpleComponent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

/**
 * Combines a hostname with a port
 */
@SuppressWarnings({"WeakerAccess", "DefaultAnnotationParam"})
@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class HostAndPort extends SimpleComponent<HostAndPort> {
    private static final int MAX_PORT = 65535;

    private final String host;
    private final Integer port;

    /**
     * Returns the validation state of the config
     *
     * @throws ConfigException if the hostname is empty or the port is not between 0 and 65536
     */
    @Override
    public void basicValidate(final String section) throws ConfigException {
        if (StringUtils.isEmpty(host) || port == null || port <= 0 || port > MAX_PORT) {
            throw new ConfigException(section, "Invalid host and port");
        }
    }

    @Override
    public String toString() {
        return host + ':' + port;
    }
}
