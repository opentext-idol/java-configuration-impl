/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration.authentication;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.SimpleComponent;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Configuration object representing a username and password combination
 */
@SuppressWarnings({"WeakerAccess", "DefaultAnnotationParam"})
@Getter
@Builder
@EqualsAndHashCode(callSuper = false)
@JsonDeserialize(builder = UsernameAndPassword.UsernameAndPasswordBuilder.class)
public class UsernameAndPassword extends SimpleComponent<UsernameAndPassword> {
    private final String username;
    private final String password;

    @JsonPOJOBuilder(withPrefix = "")
    public static class UsernameAndPasswordBuilder {
    }
}
