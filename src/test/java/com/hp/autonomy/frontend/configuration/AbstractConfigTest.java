/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

import com.hp.autonomy.frontend.configuration.validation.OptionalConfigurationComponent;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;

public class AbstractConfigTest {

    private ConcreteConfigurationComponent host;
    private ConcreteConfigurationComponent port;
    private EnabledConfigurationComponent cake;
    private ConcreteConfig concreteConfig;

    @Before
    public void setUp() {
        host = new ConcreteConfigurationComponent();
        port = new ConcreteConfigurationComponent();
        cake = new EnabledConfigurationComponent();
        concreteConfig = new ConcreteConfig(host, port, cake);
    }

    @Test
    public void testGetValidationMap() {
        final Map<String, OptionalConfigurationComponent<?>> map = concreteConfig.getValidationMap();

        assertThat(map.keySet(), hasSize(3));

        assertEquals(host, map.get("host"));
        assertEquals(port, map.get("port"));
        assertEquals(cake, map.get("cake"));
    }

    @Test
    public void testGetEnabledValidationMap() {
        final Map<String, OptionalConfigurationComponent<?>> map = concreteConfig.getEnabledValidationMap();

        assertThat(map.keySet(), hasSize(1));

        assertThat(map.get("host"), is(nullValue()));
        assertThat(map.get("port"), is(nullValue()));
        assertEquals(cake, map.get("cake"));
    }

    @SuppressWarnings("unused")
    private static class ConcreteConfig extends AbstractConfig<ConcreteConfig> {

        private final ConcreteConfigurationComponent host;
        private final ConcreteConfigurationComponent port;
        private final EnabledConfigurationComponent cake;
        private static final String FOO = "bar";

        ConcreteConfig(
                final ConcreteConfigurationComponent host,
                final ConcreteConfigurationComponent port,
                final EnabledConfigurationComponent cake
        ) {
            this.host = host;
            this.port = port;
            this.cake = cake;
        }

        public ConcreteConfigurationComponent getHost() {
            return host;
        }

        public ConcreteConfigurationComponent getPort() {
            return port;
        }

        public String getFoo() {
            return FOO;
        }

        @Override
        public void basicValidate(final String section) throws ConfigException {
        }

        @Override
        public ConcreteConfig merge(final ConcreteConfig other) {
            return null;
        }
    }

    private static class ConcreteConfigurationComponent implements OptionalConfigurationComponent<ConcreteConfigurationComponent> {
        @Override
        public ConcreteConfigurationComponent merge(final ConcreteConfigurationComponent other) {
            return null;
        }

        @Override
        public void basicValidate(final String section) throws ConfigException {

        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    }

    private static class EnabledConfigurationComponent implements OptionalConfigurationComponent<EnabledConfigurationComponent> {
        @Override
        public EnabledConfigurationComponent merge(final EnabledConfigurationComponent other) {
            return null;
        }

        @Override
        public void basicValidate(final String section) throws ConfigException {

        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }

}
