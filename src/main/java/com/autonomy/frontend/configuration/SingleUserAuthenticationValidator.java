package com.autonomy.frontend.configuration;

import com.hp.autonomy.frontend.configuration.ConfigService;
import lombok.Setter;

/*
 * $Id:$
 *
 * Copyright (c) 2014, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */
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
