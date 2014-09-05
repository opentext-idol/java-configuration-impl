/*
 * Copyright 2013-2014 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

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
