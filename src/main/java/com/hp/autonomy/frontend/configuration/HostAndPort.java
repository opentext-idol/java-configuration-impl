/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
public class HostAndPort {
    private final String host;
    private final Integer port;

    public HostAndPort(@JsonProperty("host") final String host, @JsonProperty("port") final Integer port) {
        this.host = host;
        this.port = port;
    }

    public HostAndPort merge(final HostAndPort other) {
        if (other == null) {
            return this;
        }

        return new HostAndPort(
            host == null ? other.host : host,
            port == null ? other.port : port
        );
    }

    public boolean validate() {
        return !StringUtils.isEmpty(host) && port > 0 && port < 65535;
    }

    @Override
    public String toString() {
        return host + ':' + port;
    }
}
