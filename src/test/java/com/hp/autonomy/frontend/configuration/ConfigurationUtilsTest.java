package com.hp.autonomy.frontend.configuration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationUtilsTest<C extends ConfigurationComponent<C>> {
    @Mock
    private C configurationComponent;
    @Mock
    private C configurationComponentDefault;
    @Mock
    private C mergedComponent;

    private Supplier<C> customMerge;
    private BiFunction<C, C, C> customMergeFunction;

    @Before
    public void setUp() {
        when(configurationComponent.merge(any())).thenReturn(mergedComponent);
        customMerge = () -> mergedComponent;
        customMergeFunction = (x, y) -> mergedComponent;
    }

    @Test
    public void mergeField() {
        final String localValue = "x";
        final String defaultValue = "y";
        assertEquals(localValue, ConfigurationUtils.mergeField(localValue, defaultValue));
    }

    @Test
    public void mergeFieldDefault() {
        final String localValue = null;
        final String defaultValue = "y";
        assertEquals(defaultValue, ConfigurationUtils.mergeField(localValue, defaultValue));
    }

    @Test
    public void mergeFieldNoDefault() {
        final String localValue = "x";
        final String defaultValue = null;
        assertEquals(localValue, ConfigurationUtils.mergeField(localValue, defaultValue));
    }

    @Test
    public void mergeMap() {
        final Map<String, String> localValue = new HashMap<>();
        localValue.put("Key1", "Value1");
        localValue.put("Key2", "Value2");
        final Map<String, String> defaultValue = new HashMap<>();
        defaultValue.put("Key2", "Value22");
        defaultValue.put("Key3", "Value3");
        final Map<String, String> mergedMap = ConfigurationUtils.mergeMap(localValue, defaultValue);
        assertThat(mergedMap, hasEntry(is("Key1"), is("Value1")));
        assertThat(mergedMap, hasEntry(is("Key2"), is("Value2")));
        assertThat(mergedMap, hasEntry(is("Key3"), is("Value3")));
    }

    @Test
    public void mergeMapDefault() {
        final Map<String, String> defaultValue = new HashMap<>();
        defaultValue.put("Key2", "Value22");
        defaultValue.put("Key3", "Value3");
        final Map<String, String> mergedMap = ConfigurationUtils.mergeMap(null, defaultValue);
        assertThat(mergedMap, hasEntry(is("Key2"), is("Value22")));
        assertThat(mergedMap, hasEntry(is("Key3"), is("Value3")));
    }

    @Test
    public void mergeMapNoDefault() {
        final Map<String, String> localValue = new HashMap<>();
        localValue.put("Key1", "Value1");
        localValue.put("Key2", "Value2");
        final Map<String, String> mergedMap = ConfigurationUtils.mergeMap(localValue, null);
        assertThat(mergedMap, hasEntry(is("Key1"), is("Value1")));
        assertThat(mergedMap, hasEntry(is("Key2"), is("Value2")));
    }

    @Test
    public void mergeComponent() {
        assertEquals(mergedComponent, ConfigurationUtils.mergeComponent(configurationComponent, configurationComponentDefault));
    }

    @Test
    public void mergeComponentDefault() {
        assertEquals(configurationComponentDefault, ConfigurationUtils.mergeComponent(null, configurationComponentDefault));
    }

    @Test
    public void mergeComponentNoDefault() {
        when(configurationComponent.merge(any())).thenReturn(configurationComponent);
        assertEquals(configurationComponent, ConfigurationUtils.mergeComponent(configurationComponent, null));
    }

    @Test
    public void customMerge() {
        assertEquals(mergedComponent, ConfigurationUtils.mergeConfiguration(configurationComponent, configurationComponentDefault, customMerge));
    }

    @Test
    public void customMergeNoDefault() {
        assertEquals(configurationComponent, ConfigurationUtils.mergeConfiguration(configurationComponent, null, customMerge));
    }

    @Test
    public void customMergeFunction() {
        assertEquals(mergedComponent, ConfigurationUtils.mergeConfiguration(configurationComponent, configurationComponentDefault, customMergeFunction));
    }

    @Test
    public void customMergeFunctionNoDefault() {
        assertEquals(configurationComponent, ConfigurationUtils.mergeConfiguration(configurationComponent, null, customMergeFunction));
    }

    @Test
    public void basicValidate() throws ConfigException {
        ConfigurationUtils.basicValidate(configurationComponent, "SomeSection");
    }

    @Test
    public void basicValidateNoComponent() throws ConfigException {
        ConfigurationUtils.basicValidate(null, "SomeSection");
    }

    @Test(expected = ConfigException.class)
    public void basicValidateError() throws ConfigException {
        doThrow(new ConfigException("section", "message")).when(configurationComponent).basicValidate(anyString());
        ConfigurationUtils.basicValidate(configurationComponent, "SomeSection");
    }
}
