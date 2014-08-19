package com.autonomy.frontend.configuration;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

/*
 * $Id$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author$ on $Date$
 */
@JsonDeserialize(builder = CasConfig.Builder.class)
@Getter
@EqualsAndHashCode
/**
 * Configuration object for CAS SSO.
 */
public class CasConfig {

    private final String casServerLoginUrl;
    private final String casServerUrlPrefix;
    private final String serverName;

    private CasConfig(final Builder builder) {
        this.casServerLoginUrl = builder.casServerLoginUrl;
        this.casServerUrlPrefix = builder.casServerUrlPrefix;
        this.serverName = builder.serverName;
    }

    public CasConfig merge(final CasConfig casConfig) {
        if(casConfig != null) {
            final Builder builder = new Builder();

            builder.setCasServerLoginUrl(this.casServerLoginUrl == null ? casConfig.casServerLoginUrl : this.casServerLoginUrl);
            builder.setCasServerUrlPrefix(this.casServerUrlPrefix == null ? casConfig.casServerUrlPrefix : this.casServerUrlPrefix);
            builder.setServerName(this.serverName == null ? casConfig.serverName : this.serverName);

            return builder.build();
        }
        else {
            return this;
        }
    }

    public void basicValidate() throws ConfigException {
        if(StringUtils.isBlank(this.getCasServerLoginUrl())
                || StringUtils.isBlank(this.getCasServerUrlPrefix())
                || StringUtils.isBlank(this.getServerName())){
            throw new ConfigException("Login", "CAS attributes have not been defined in the config file. Please specify them in the config file");
        }
    }

    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder {
        private String casServerLoginUrl;
        private String casServerUrlPrefix;
        private String serverName;

        public Builder setCasServerLoginUrl(final String casServerLoginUrl) {
            this.casServerLoginUrl = casServerLoginUrl;
            return this;
        }

        public Builder setCasServerUrlPrefix(final String casServerUrlPrefix) {
            this.casServerUrlPrefix = casServerUrlPrefix;
            return this;
        }

        public Builder setServerName(final String serverName) {
            this.serverName = serverName;
            return this;
        }

        public CasConfig build() {
            return new CasConfig(this);
        }
    }

}
