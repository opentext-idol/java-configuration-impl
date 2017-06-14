package com.hp.autonomy.frontend.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationUtilsTest<C extends ConfigurationComponent<C>> {
    @Mock
    private C configurationComponent;
    @Mock
    private C configurationComponentDefault;
    @Mock
    private C mergedComponent;

    private Supplier<C> customMerge;

    @Before
    public void setUp() {
        when(configurationComponent.merge(any())).thenReturn(mergedComponent);
        customMerge = () -> mergedComponent;
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

    @Test
    public void defaultMerge() {
        final Map<String, String> localMap = new HashMap<>();
        localMap.put("Key1", "Value1");
        localMap.put("Key2", "Value2");
        final Map<String, String> defaultMap = new HashMap<>();
        defaultMap.put("Key2", "Value22");
        defaultMap.put("Key3", "Value3");
        final ConfigurationComponent<?> subComponent = mock(ConfigurationComponent.class);
        final TestObject local = TestObject.builder()
                .map(localMap)
                .simple(true)
                .subComponent(subComponent)
                .build();
        final TestObject defaults = TestObject.builder()
                .map(defaultMap)
                .subComponent(mock(ConfigurationComponent.class))
                .build();
        final TestObject merged = ConfigurationUtils.defaultMerge(local, defaults);
        assertThat(merged.map, hasEntry(is("Key1"), is("Value1")));
        assertThat(merged.map, hasEntry(is("Key2"), is("Value2")));
        assertThat(merged.map, hasEntry(is("Key3"), is("Value3")));
        assertThat(merged.simple, is(true));
        verify(subComponent).merge(any());
    }

    @Test
    public void defaultMergeWithSingularList() {
        final SingularListTestObject object1 = SingularListTestObject.builder()
                .value("cat")
                .value("dog")
                .build();

        final SingularListTestObject object2 = SingularListTestObject.builder()
                .value("budgie")
                .build();

        assertThat(object1.merge(object2).getValues(), contains("cat", "dog"));
        assertThat(object2.merge(object1).getValues(), contains("budgie"));
    }

    @Test(expected = ConfigurationUtils.ConfigRuntimeException.class)
    public void invalidDefaultMerge() {
        ConfigurationUtils.defaultMerge(new BadObject(null, null, false), new BadObject(null, null, false));
    }

    @Test
    public void defaultValidate() throws ConfigException {
        final ConfigurationComponent<?> subComponent = mock(ConfigurationComponent.class);
        final TestObject local = TestObject.builder()
                .simple(true)
                .subComponent(subComponent)
                .build();
        ConfigurationUtils.defaultValidate(local, null);
        verify(subComponent).basicValidate(anyString());
    }

    @Test(expected = ConfigException.class)
    public void invalidDefaultValidate() throws ConfigException {
        ConfigurationUtils.defaultValidate(new AnotherBadObject(), null);
    }

    @Getter
    @Builder
    private static class SingularListTestObject extends SimpleComponent<SingularListTestObject> {
        @Singular
        private final List<String> values;
    }

    @Getter
    @Builder
    private static class TestObject extends SimpleComponent<TestObject> {
        private final Map<String, String> map;
        private final ConfigurationComponent<?> subComponent;
        private final boolean simple;
    }

    @SuppressWarnings("unused")
    @Getter
    @AllArgsConstructor
    private static class BadObject extends SimpleComponent<BadObject> {
        private final Map<String, String> map;
        private final ConfigurationComponent<?> subComponent;
        private final boolean simple;
    }

    @SuppressWarnings("unused")
    private static class AnotherBadObject extends SimpleComponent<AnotherBadObject> {
        private ConfigurationComponent<?> badComponent;

        @SuppressWarnings("ProhibitedExceptionThrown")
        public ConfigurationComponent<?> getBadComponent() {
            throw new RuntimeException();
        }
    }
}
