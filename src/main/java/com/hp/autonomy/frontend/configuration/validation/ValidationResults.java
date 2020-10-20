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

package com.hp.autonomy.frontend.configuration.validation;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A collection of {@link ValidationResult}
 */
public class ValidationResults {

    private final Map<String, ValidationResult<?>> map;
    private final boolean valid;

    private ValidationResults(final Map<String, ValidationResult<?>> map) {
        this.map = Collections.unmodifiableMap(map);
        boolean valid = true;

        for (final ValidationResult<?> result : map.values()) {
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
