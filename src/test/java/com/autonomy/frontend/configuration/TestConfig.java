package com.autonomy.frontend.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/*
 * $Id:$
 *
 * Copyright (c) 2014, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */
class TestConfig implements AuthenticationConfig<TestConfig> {
    private Authentication<?> authentication;

    @Override
    public TestConfig generateDefaultLogin() {
        return null;
    }

    @Override
    public TestConfig withHashedPasswords() {
        return null;
    }

    @Override
    public Authentication<?> getAuthentication() {
        return authentication;
    }

    @Override
    public TestConfig withoutDefaultLogin() {
        return null;
    }
}