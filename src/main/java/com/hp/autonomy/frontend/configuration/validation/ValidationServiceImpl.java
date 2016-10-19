/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.validation;

import com.hp.autonomy.frontend.configuration.Config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reference implementation of ValidationService
 */
@SuppressWarnings("WeakerAccess")
public class ValidationServiceImpl<T extends Config<T>> implements ValidationService<T> {

    private final Map<Class<?>, Validator<?>> validators = new ConcurrentHashMap<>();

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException If no validator is found for the given component
     */
    @Override
    public ValidationResult<?> validate(final OptionalConfigurationComponent<?> configurationComponent) {
        if (configurationComponent instanceof ValidatingConfigurationComponent) {
            @SuppressWarnings("rawtypes")
            final ValidatingConfigurationComponent<?> validatingComponent = (ValidatingConfigurationComponent) configurationComponent;
            return validatingComponent.validate();
        } else {
            final Class<?> componentClass = configurationComponent.getClass();
            final Validator<?> validator = validators.get(componentClass);
            if (validator != null) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                final ValidationResult<?> validationResult = ((Validator) validator).validate(configurationComponent);
                return validationResult;
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

    private ValidationResults validateConfig(final Map<String, OptionalConfigurationComponent<?>> components) {
        final ValidationResults.Builder builder = new ValidationResults.Builder();

        for (final Map.Entry<String, OptionalConfigurationComponent<?>> stringOptionalConfigurationComponentEntry : components.entrySet()) {
            final OptionalConfigurationComponent<?> optionalConfigurationComponent = stringOptionalConfigurationComponentEntry.getValue();

            final ValidationResult<?> result = validate(optionalConfigurationComponent);

            builder.put(stringOptionalConfigurationComponentEntry.getKey(), result);
        }

        return builder.build();
    }

    /**
     * Set the validators used by the service.  This will clear any existing validators.  This method is thread safe.
     *
     * @param validators The new validators to be used by the service
     */
    public void setValidators(final Iterable<Validator<?>> validators) {
        this.validators.clear();

        for (final Validator<?> validator : validators) {
            // this ensures we map OptionalConfigurationComponent classes to validators of the same class
            this.validators.put(validator.getSupportedClass(), validator);
        }
    }

}
