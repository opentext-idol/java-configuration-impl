/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.authentication;

import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.frontend.configuration.validation.ValidationResult;
import com.hp.autonomy.frontend.configuration.validation.Validator;
import lombok.Setter;

public class SingleUserAuthenticationValidator implements Validator<SingleUserAuthentication> {

    @Setter
    private ConfigService<? extends AuthenticationConfig<?>> configService;

    @Override
    public ValidationResult<?> validate(final SingleUserAuthentication config) {
        return config.validate(configService);
    }

    @Override
    public Class<SingleUserAuthentication> getSupportedClass() {
        return SingleUserAuthentication.class;
    }
}
