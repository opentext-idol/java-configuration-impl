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

package com.hp.autonomy.frontend.configuration.redis;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.SimpleComponent;
import com.hp.autonomy.frontend.configuration.server.HostAndPort;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;

/**
 * Configuration for a Redis server. This allows both a single Redis or a Redis Sentinel configuration
 */
@SuppressWarnings({"WeakerAccess", "InstanceVariableOfConcreteClass", "MismatchedQueryAndUpdateOfCollection"})
@Getter
@Builder
@ToString
@JsonDeserialize(builder = RedisConfig.RedisConfigBuilder.class)
public class RedisConfig extends SimpleComponent<RedisConfig> {
    private static final String CONFIG_SECTION = "redis";

    private final String masterName;
    private final String password;
    private final HostAndPort address;
    @Singular
    private final Collection<HostAndPort> sentinels;
    private final Integer database;

    /**
     * If true, indicates that application should configure redis. Otherwise, the application can assume that redis has
     * been configured correctly. This is useful because some secure redis instances (eg Azure) don't allow clients to
     * run the CONFIG command.
     */
    private final Boolean autoConfigure;

    /**
     * Validates the configuration
     *
     * @throws ConfigException If either:
     *                         <ul>
     *                         <li>address is non null and invalid</li>
     *                         <li>address is null and sentinels is null or empty </li>
     *                         <li>sentinels is non null and non empty and masterName is null or blank</li>
     *                         <li>any sentinels are invalid</li>
     *                         </ul>
     */
    @Override
    public void basicValidate(final String section) throws ConfigException {
        super.basicValidate(CONFIG_SECTION);

        if (address == null && (sentinels == null || sentinels.isEmpty())) {
            throw new ConfigException(CONFIG_SECTION, "Redis configuration requires either an address or at least one sentinel to connect to");
        }

        if (sentinels != null && !sentinels.isEmpty()) {
            if (StringUtils.isBlank(masterName)) {
                throw new ConfigException(CONFIG_SECTION, "Redis configuration requires a masterName when connecting to sentinel");
            }

            for (final HostAndPort sentinel : sentinels) {
                sentinel.basicValidate(CONFIG_SECTION);
            }
        }
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class RedisConfigBuilder {
    }
}
