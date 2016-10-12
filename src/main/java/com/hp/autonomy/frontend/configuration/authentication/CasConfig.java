/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.authentication;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.SimpleComponent;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

/**
 * Configuration object for CAS SSO.
 */
@SuppressWarnings("WeakerAccess")
@JsonDeserialize(builder = CasConfig.CasConfigBuilder.class)
@Getter
@Builder
public class CasConfig extends SimpleComponent<CasConfig> {
    private final String casServerLoginUrl;
    private final String casServerUrlPrefix;
    private final String serverName;

    @Override
    public void basicValidate(final String section) throws ConfigException {
        if (StringUtils.isBlank(casServerLoginUrl)
                || StringUtils.isBlank(casServerUrlPrefix)
                || StringUtils.isBlank(serverName)) {
            throw new ConfigException("Login", "CAS attributes have not been defined in the config file. Please specify them in the config file");
        }
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class CasConfigBuilder {
    }

}
