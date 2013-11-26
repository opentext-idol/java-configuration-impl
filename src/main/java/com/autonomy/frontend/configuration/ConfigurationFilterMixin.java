package com.autonomy.frontend.configuration;

import com.fasterxml.jackson.annotation.JsonFilter;

/*
 * $Id:$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author:$ on $Date:$
 */

/**
 * Jackson Mixin that adds a {@code @JsonFilter("configurationFilter")} to classes.  If a filter with this name is
 * provided to the {@link com.fasterxml.jackson.databind.ObjectMapper}, configuration elements can be removed when the
 * config is saved.
 *
 * @see <a href="http://wiki.fasterxml.com/JacksonMixInAnnotations">http://wiki.fasterxml.com/JacksonMixInAnnotations</a>
 */
@JsonFilter("configurationFilter")
public abstract class ConfigurationFilterMixin {}
