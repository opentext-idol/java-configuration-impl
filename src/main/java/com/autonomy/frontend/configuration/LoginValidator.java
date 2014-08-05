package com.autonomy.frontend.configuration;

import com.autonomy.aci.client.services.AciService;

/*
 * $Id:$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */

/**
 * A validator for {@link Login}
 */
public class LoginValidator implements Validator<Login> {

    private AciService aciService;

    private ConfigService<? extends LoginConfig<?>> configService;

    @Override
    public ValidationResult<?> validate(final Login login) {
        return login.validate(aciService, configService);
    }

    @Override
    public Class<Login> getSupportedClass() {
        return Login.class;
    }

    public void setAciService(final AciService aciService) {
        this.aciService = aciService;
    }

    public void setConfigService(final ConfigService<? extends LoginConfig<?>> configService) {
        this.configService = configService;
    }
}
