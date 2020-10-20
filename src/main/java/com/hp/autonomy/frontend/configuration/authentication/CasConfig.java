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

package com.hp.autonomy.frontend.configuration.authentication;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.SimpleComponent;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

/**
 * Configuration object for CAS SSO.
 */
@SuppressWarnings("WeakerAccess")
@JsonDeserialize(builder = CasConfig.CasConfigBuilder.class)
@Getter
@Builder
@ToString
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
