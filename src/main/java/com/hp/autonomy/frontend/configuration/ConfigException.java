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

package com.hp.autonomy.frontend.configuration;

import lombok.Getter;

/**
 * Exception thrown when errors are found in configuration files and objects.
 */
public class ConfigException extends Exception {

    private static final long serialVersionUID = -6631508891944406923L;

    @Getter
    private final String section;

    public ConfigException(final String section, final String message) {
        super(section + ": " + message);

        this.section = section;
    }

    public ConfigException(final String section, final Throwable cause) {
        super(section + ": " + cause.getMessage(), cause);

        this.section = section;
    }
}
