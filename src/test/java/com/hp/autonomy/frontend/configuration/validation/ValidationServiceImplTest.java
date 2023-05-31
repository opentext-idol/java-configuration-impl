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

package com.hp.autonomy.frontend.configuration.validation;

import com.hp.autonomy.frontend.configuration.ConfigException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ValidationServiceImplTest {

    private ValidationServiceImpl<?> validationService;

    @Before
    public void setUp() {
        validationService = new ValidationServiceImpl();

        final Collection<Validator<?>> validators = new HashSet<>();
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

    private static class ElvisConfiguration implements OptionalConfigurationComponent<ElvisConfiguration> {

        @Override
        public Boolean getEnabled() {
            return true;
        }

        @Override
        public ElvisConfiguration merge(final ElvisConfiguration other) {
            return null;
        }

        @Override
        public void basicValidate(final String section) throws ConfigException {

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

    private static class CakeConfiguration implements OptionalConfigurationComponent<CakeConfiguration> {

        @Override
        public Boolean getEnabled() {
            return true;
        }

        @Override
        public CakeConfiguration merge(final CakeConfiguration other) {
            return null;
        }

        @Override
        public void basicValidate(final String section) throws ConfigException {

        }
    }
}
