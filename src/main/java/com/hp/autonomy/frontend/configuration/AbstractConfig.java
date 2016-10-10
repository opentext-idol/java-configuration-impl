/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hp.autonomy.frontend.configuration.validation.OptionalConfigurationComponent;

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
                    result.put(field.getName(), (OptionalConfigurationComponent<?>) o);
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

            if (!entry.getValue().isEnabled()) {
                iterator.remove();
            }
        }

        return validationMap;
    }
}
