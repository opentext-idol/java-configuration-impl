/*
 * Copyright 2013-2015 Open Text.
 *
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License.
 *
 * The only warranties for products and services of Open Text and its affiliates
 * and licensors ("Open Text") are as may be set forth in the express warranty
 * statements accompanying such products and services. Nothing herein should be
 * construed as constituting an additional warranty. Open Text shall not be
 * liable for technical or editorial errors or omissions contained herein. The
 * information contained herein is subject to change without notice.
 */

package com.hp.autonomy.frontend.configuration.server;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.SimpleComponent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * Combines a hostname with a port
 */
@SuppressWarnings({"WeakerAccess", "DefaultAnnotationParam"})
@Getter
@Builder
@JsonDeserialize(builder = HostAndPort.HostAndPortBuilder.class)
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

    @JsonPOJOBuilder(withPrefix="")
    public static class HostAndPortBuilder { }
}
