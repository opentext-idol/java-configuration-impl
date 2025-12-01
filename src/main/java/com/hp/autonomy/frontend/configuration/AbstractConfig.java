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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hp.autonomy.frontend.configuration.validation.OptionalConfigurationComponent;
import org.apache.commons.lang3.BooleanUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation of {@link Config} which provides a reference implementation of some methods.
 *
 * @param <T> The type of the configuration object
 */
@SuppressWarnings("WeakerAccess")
public abstract class AbstractConfig<T extends AbstractConfig<T>> implements Config<T> {

    /*
     * Uses reflection so as to reduce the number of places the names must be typed in by hand
     */
    @Override
    @JsonIgnore
    public Map<String, OptionalConfigurationComponent<?>> getValidationMap() {
        // Use getDeclaredFields as the fields will probably be private
        final Field[] fields = getClass().getDeclaredFields();
        final Map<String, OptionalConfigurationComponent<?>> result = new HashMap<>();

        for (final Field field : fields) {
            final boolean oldValue = field.isAccessible();

            try {
                field.setAccessible(true);
                final Object o = field.get(this);

                // if o is null this is false
                if (o instanceof OptionalConfigurationComponent) {
                    final JsonProperty ann = field.getAnnotation(JsonProperty.class);
                    final String key;

                    if (ann != null) {
                        key = ann.value();
                    }
                    else {
                        key = field.getName();
                    }

                    result.put(key, (OptionalConfigurationComponent<?>) o);
                }
            } catch (final IllegalAccessException e) {
                throw new AssertionError("Your JVM does not allow you to run this code.", e);
            } finally {
                //noinspection ThrowFromFinallyBlock
                field.setAccessible(oldValue);
            }
        }

        return result;
    }

    @Override
    @JsonIgnore
    public Map<String, OptionalConfigurationComponent<?>> getEnabledValidationMap() {
        final Map<String, OptionalConfigurationComponent<?>> validationMap = getValidationMap();

        final Iterator<Map.Entry<String, OptionalConfigurationComponent<?>>> iterator = validationMap.entrySet().iterator();

        while (iterator.hasNext()) {
            final Map.Entry<String, OptionalConfigurationComponent<?>> entry = iterator.next();

            if (BooleanUtils.isFalse(entry.getValue().getEnabled())) {
                iterator.remove();
            }
        }

        return validationMap;
    }
}
