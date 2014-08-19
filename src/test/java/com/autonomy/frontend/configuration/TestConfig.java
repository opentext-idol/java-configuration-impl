package com.autonomy.frontend.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/*
 * $Id:$
 *
 * Copyright (c) 2014, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */
class TestConfig<T extends Authentication<T>> implements AuthenticationConfig<T, TestConfig<T>> {
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
    private Authentication<?> authentication;

    @Override
    public TestConfig<T> generateDefaultLogin() {
        return null;
    }

    @Override
    public TestConfig<T> withHashedPasswords() {
        return null;
    }

    @Override
    public T getAuthentication() {
        //noinspection unchecked
        return (T) authentication;
    }

    @Override
    public TestConfig<T> withoutDefaultLogin() {
        return null;
    }
}