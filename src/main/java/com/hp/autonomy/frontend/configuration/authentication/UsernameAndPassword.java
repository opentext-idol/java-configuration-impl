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
