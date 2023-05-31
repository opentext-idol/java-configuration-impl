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

package com.hp.autonomy.frontend.configuration.filter;

import com.fasterxml.jackson.annotation.JsonFilter;

/**
 * Jackson Mixin that adds a {@code @JsonFilter("configurationFilter")} to classes.  If a filter with this name is
 * provided to the {@link com.fasterxml.jackson.databind.ObjectMapper}, configuration elements can be removed when the
 * config is saved.
 *
 * @see <a href="http://wiki.fasterxml.com/JacksonMixInAnnotations">http://wiki.fasterxml.com/JacksonMixInAnnotations</a>
 */
@JsonFilter("configurationFilter")
public abstract class ConfigurationFilterMixin {
}
