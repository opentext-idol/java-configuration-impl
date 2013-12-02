package com.autonomy.frontend.configuration;

import java.util.HashMap;
import java.util.Map;

/*
 * $Id:$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */

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
