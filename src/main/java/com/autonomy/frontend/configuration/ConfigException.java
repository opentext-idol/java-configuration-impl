package com.autonomy.frontend.configuration;

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
