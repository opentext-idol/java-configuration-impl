package com.hp.autonomy.frontend.configuration;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collections;
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
 * A collection of {@link ValidationResult}
 */
public class ValidationResults {

    private final Map<String, ValidationResult<?>> map;
    private final boolean valid;

    private ValidationResults(final Map<String, ValidationResult<?>> map) {
        this.map = Collections.unmodifiableMap(map);
        boolean valid = true;

        for(final ValidationResult<?> result : map.values()) {
            valid = valid && result.isValid();
        }

        this.valid = valid;
    }

    @JsonAnyGetter
    public Map<String, ValidationResult<?>> getMap() {
        return map;
    }

    /**
     * @return true if every wrapped ValidationResult is valid; false otherwise
     */
    @JsonIgnore
    public boolean isValid() {
        return valid;
    }

    public static class Builder {

        private final Map<String, ValidationResult<?>> map = new HashMap<>();

        public Builder put(final String key, final ValidationResult<?> value) {
            map.put(key, value);

            return this;
        }

        public ValidationResults build() {
            return new ValidationResults(map);
        }

    }

}
