/*
 * Copyright 2013-2014 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Associates a boolean with an object of arbitrary type which may provide additional information in
 * cases of validation failure or as side effects to the validation process.
 *
 * @param <T> The type of the additional data.  This should be {@link Void} if no data is to be returned.
 */
public class ValidationResult<T> {

    private final T data;
    private final boolean isValid;

    public ValidationResult(final boolean valid) {
        this(valid, null);
    }

    public ValidationResult(final boolean valid, final T data) {
        this.data = data;
        isValid = valid;
    }

    /**
     * Returns the data associated with the validation.  The presence of data does not imply anything about
     * the success or failure of the validation.
     *
     * @return The data associated with the validation.
     */
    public T getData() {
        return data;
    }

    public boolean isValid() {
        return isValid;
    }
}
