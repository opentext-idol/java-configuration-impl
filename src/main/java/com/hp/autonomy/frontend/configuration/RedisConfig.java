/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;

@Data
@JsonDeserialize(builder = RedisConfig.Builder.class)
public class RedisConfig {
    private final String masterName;
    private final String password;
    private final HostAndPort address;
    private final Set<HostAndPort> sentinels;

    private RedisConfig(final Builder builder) {
        masterName = builder.masterName;
        password = builder.password;
        address = builder.address;
        sentinels = builder.sentinels;
    }

    public void basicValidate() throws ConfigException {
        if ((address != null && !address.validate()) && (sentinels == null || sentinels.isEmpty())) {
            throw new ConfigException("redis", "Redis configuration requires either an address or at least one sentinel to connect to");
        }

        if (sentinels != null && !sentinels.isEmpty() && StringUtils.isBlank(masterName)) {
            throw new ConfigException("redis", "Redis configuration requires a masterName when connecting to sentinel");
        }
    }

    public RedisConfig merge(final RedisConfig other) {
        final Builder builder = new Builder(this);
        if (masterName == null) builder.masterName = other.masterName;
        if (password == null) builder.password = other.password;
        if (sentinels == null) builder.sentinels = other.sentinels;
        builder.address = address == null ? other.address : address.merge(other.address);
        return builder.build();
    }

    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {
        private String masterName;
        private String password;
        private HostAndPort address;
        private Set<HostAndPort> sentinels;

        public Builder(final RedisConfig config) {
            masterName = config.masterName;
            password = config.password;
            address = config.address;
            sentinels = config.sentinels;
        }

        public RedisConfig build() {
            return new RedisConfig(this);
        }
    }
}
