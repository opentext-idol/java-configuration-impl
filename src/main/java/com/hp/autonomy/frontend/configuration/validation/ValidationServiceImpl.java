/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.validation;

import com.hp.autonomy.frontend.configuration.Config;
import com.hp.autonomy.frontend.configuration.ConfigurationComponent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reference implementation of ValidationService
 */
public class ValidationServiceImpl<T extends Config<T>> implements ValidationService<T> {

    private final Map<Class<?>, Validator<?>> validators = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException If no validator is found for the given component
     */
    @Override
    public <T extends ConfigurationComponent> ValidationResult<?> validate(final T configurationComponent) {
        if (configurationComponent instanceof ValidatingConfigurationComponent) {
            final ValidatingConfigurationComponent validatingComponent = (ValidatingConfigurationComponent) configurationComponent;

            return validatingComponent.validate();
        } else {
            // getClass on a T returns a Class<T>
            @SuppressWarnings("unchecked")
            final Class<T> componentClass = (Class<T>) configurationComponent.getClass();

            // get(Class<T extend ConfigurationComponent<T>>) will always return a validator for the class
            @SuppressWarnings("unchecked")
            final Validator<T> validator = (Validator<T>) validators.get(componentClass);

            if (validator != null) {
                return validator.validate(configurationComponent);
            } else {
                throw new IllegalArgumentException("No validator for class " + componentClass.getCanonicalName());
            }
        }
    }

    @Override
    public ValidationResults validateConfig(final T config) {
        return validateConfig(config.getValidationMap());
    }

    @Override
    public ValidationResults validateEnabledConfig(final T config) {
        return validateConfig(config.getEnabledValidationMap());
    }

    private ValidationResults validateConfig(final Map<String, ConfigurationComponent> components) {
        final ValidationResults.Builder builder = new ValidationResults.Builder();

        for (final String component : components.keySet()) {
            final ConfigurationComponent configurationComponent = components.get(component);

            final ValidationResult<?> result = validate(configurationComponent);

            builder.put(component, result);
        }

        return builder.build();
    }

    /**
     * Set the validators used by the service.  This will clear any existing validators.  This method is thread safe.
     *
     * @param validators The new validators to be used by the service
     */
    public void setValidators(final Set<Validator<?>> validators) {
        this.validators.clear();

        for (final Validator<?> validator : validators) {
            // this ensures we map ConfigurationComponent classes to validators of the same class
            this.validators.put(validator.getSupportedClass(), validator);
        }
    }

}
