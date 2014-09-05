package com.hp.autonomy.frontend.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * $Id$
 * <p/>
 * Copyright (c) 2013, Autonomy Systems Ltd.
 * <p/>
 * Last modified by $Author$ on $Date$
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigResponse<T> {
	private T config;
	private String configPath;
	private String configEnvVariable;
}
