/*
 * Copyright 2013-2014 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

import com.fasterxml.jackson.annotation.JsonFilter;

/**
 * Jackson Mixin that adds a {@code @JsonFilter("configurationFilter")} to classes.  If a filter with this name is
 * provided to the {@link com.fasterxml.jackson.databind.ObjectMapper}, configuration elements can be removed when the
 * config is saved.
 *
 * @see <a href="http://wiki.fasterxml.com/JacksonMixInAnnotations">http://wiki.fasterxml.com/JacksonMixInAnnotations</a>
 */
@JsonFilter("configurationFilter")
public abstract class ConfigurationFilterMixin {}
