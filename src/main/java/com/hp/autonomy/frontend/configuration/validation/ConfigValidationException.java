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

package com.hp.autonomy.frontend.configuration.validation;

/**
 * Exception thrown when updating a config if the config is invalid.
 */
public class ConfigValidationException extends Exception {

    private static final long serialVersionUID = -8645510626116828438L;

    private final ValidationResults validationErrors;

    public ConfigValidationException(final ValidationResults validationErrors) {
        this.validationErrors = validationErrors;
    }

    /**
     * @return The results of the validation failure that caused the exception
     */
    public ValidationResults getValidationErrors() {
        return validationErrors;
    }
}
