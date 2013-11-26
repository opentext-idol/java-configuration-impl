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

    private final Map<String, Boolean> validationErrors = new HashMap<>();

    public ConfigValidationException(final ValidationResults validationErrors) {
        for(final Map.Entry<String, ValidationResult<?>> entry : validationErrors.getMap().entrySet()) {
            this.validationErrors.put(entry.getKey(), entry.getValue().isValid());
        }
    }

    /**
     * @return A map from keys in the configuration to boolean; false means the section was invalid.
     */
    public Map<String, Boolean> getValidationErrors() {
        return validationErrors;
    }
}
