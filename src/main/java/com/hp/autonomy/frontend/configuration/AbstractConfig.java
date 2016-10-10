/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Implementation of {@link Config} which provides a reference implementation of some methods.
 *
 * @param <T> The type of the configuration object
 */
public abstract class AbstractConfig<T extends AbstractConfig<T>> implements Config<T> {

    /*
     * Uses reflection so as to reduce the number of places the names must be typed in by hand
     */
    @Override
    @JsonIgnore
    public Map<String, ConfigurationComponent> getValidationMap() {
        // Use getDeclaredFields as the fields will probably be private
        final Field[] fields = this.getClass().getDeclaredFields();
        final Map<String, ConfigurationComponent> result = new HashMap<>();

        for (final Field field : fields) {
            final boolean oldValue = field.isAccessible();

            try {
                field.setAccessible(true);
                final Object o = field.get(this);

                // if o is null this is false
                if (o instanceof ConfigurationComponent) {
                    result.put(field.getName(), (ConfigurationComponent) o);
                }
            } catch (IllegalAccessException e) {
                throw new AssertionError("Your JVM does not allow you to run this code.", e);
            } finally {
                field.setAccessible(oldValue);
            }
        }

        return result;
    }

    @Override
    @JsonIgnore
    public Map<String, ConfigurationComponent> getEnabledValidationMap() {
        final Map<String, ConfigurationComponent> validationMap = getValidationMap();

        final Iterator<Map.Entry<String, ConfigurationComponent>> iterator = validationMap.entrySet().iterator();

        while (iterator.hasNext()) {
            final Map.Entry<String, ConfigurationComponent> entry = iterator.next();

            if (!entry.getValue().isEnabled()) {
                iterator.remove();
            }
        }

        return validationMap;
    }
}
