/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.validation;

import com.hp.autonomy.frontend.configuration.ConfigurationComponent;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ValidationServiceImplTest {

    private ValidationServiceImpl<?> validationService;

    @Before
    public void setUp() {
        validationService = new ValidationServiceImpl();

        final HashSet<Validator<?>> validators = new HashSet<>();
        validators.add(new ElvisValidator());
        validators.add(new CakeValidator());

        validationService.setValidators(validators);
    }

    @Test
    public void testValidate() {
        assertThat(validationService.validate(new ElvisConfiguration()).isValid(), is(true));
        assertThat(validationService.validate(new ElvisConfiguration()).getData(), is(nullValue()));
        assertThat(validationService.validate(new CakeConfiguration()).isValid(), is(false));
        assertThat(validationService.validate(new CakeConfiguration()).getData(), is(nullValue()));
    }

    private static class ElvisValidator implements Validator<ElvisConfiguration> {

        @Override
        public ValidationResult<?> validate(final ElvisConfiguration config) {
            return new ValidationResult<Void>(true);
        }

        @Override
        public Class<ElvisConfiguration> getSupportedClass() {
            return ElvisConfiguration.class;
        }
    }

    private static class ElvisConfiguration implements ConfigurationComponent {

        @Override
        public boolean isEnabled() {
            return true;
        }

    }

    private static class CakeValidator implements Validator<CakeConfiguration> {

        @Override
        public ValidationResult<?> validate(final CakeConfiguration config) {
            return new ValidationResult<Void>(false);
        }

        @Override
        public Class<CakeConfiguration> getSupportedClass() {
            return CakeConfiguration.class;
        }
    }

    private static class CakeConfiguration implements ConfigurationComponent {

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
