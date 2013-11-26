package com.autonomy.frontend.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/*
 * $Id:$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */

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

    public T getData() {
        return data;
    }

    public boolean isValid() {
        return isValid;
    }
}
