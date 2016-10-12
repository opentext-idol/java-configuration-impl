package com.hp.autonomy.frontend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;
import org.springframework.core.ResolvableType;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Standard test for configuration components
 *
 * @param <C> the type of the component to test
 */
@SuppressWarnings("WeakerAccess")
public abstract class ConfigurationComponentTest<C extends ConfigurationComponent<C>> {
    protected JacksonTester<C> json;

    @Before
    public void setUp() {
        final ObjectMapper objectMapper = new ObjectMapper();
        json = new JacksonTester<>(getClass(), ResolvableType.forClass(getType()), objectMapper);
    }

    /**
     * The type of the component to test
     *
     * @return the type of the component to test
     */
    protected abstract Class<C> getType();

    /**
     * A valid component object instance
     * This instance will be used as the basis of a merge and is expected to pass validation
     *
     * @return a valid component object instance
     */
    protected abstract C constructComponent();

    /**
     * Some sample json content
     * This will be parsed and used as the "defaults" in the merge test
     *
     * @return sample json as a String
     * @throws IOException
     */
    protected abstract String sampleJson() throws IOException;

    /**
     * Validation to perform on the json parsed from the supplied object
     *
     * @param json parsed json
     */
    protected abstract void validateJson(final JsonContent<C> json);

    /**
     * Validation to perform on the object generated from the sample json
     *
     * @param component the generated object
     */
    protected abstract void validateParsedComponent(final ObjectContent<C> component);

    /**
     * Validation to perform on the result of the merge between the sample object and the sample json
     *
     * @param mergedComponent the merged object
     */
    protected abstract void validateMergedComponent(final ObjectContent<C> mergedComponent);

    @Test
    public void toJson() throws IOException {
        final C component = constructComponent();
        final JsonContent<C> jsonContent = json.write(component);
        validateJson(jsonContent);
    }

    @Test
    public void fromJson() throws IOException {
        final String sampleJson = sampleJson();
        final ObjectContent<C> component = json.parse(sampleJson);
        validateParsedComponent(component);
    }

    @Test
    public void jsonSymmetry() throws IOException {
        final String sampleJson = sampleJson();
        final C component = json.parse(sampleJson).getObject();
        final JsonContent<C> jsonContent = json.write(component);
        assertThat(jsonContent).isEqualToJson(sampleJson);
    }

    @Test
    public void merge() throws IOException {
        final C component = constructComponent();
        final String sampleJson = sampleJson();
        final C defaults = json.parse(sampleJson).getObject();
        final C mergedComponent = component.merge(defaults);
        validateMergedComponent(new ObjectContent<>(ResolvableType.forClass(getType()), mergedComponent));
    }

    @Test
    public void mergeWithNothing() {
        final C component = constructComponent();
        final C mergedComponent = component.merge(null);
        assertEquals(component, mergedComponent);
    }

    @Test
    public void validateGoodConfig() throws ConfigException {
        final C component = constructComponent();
        component.basicValidate("configSection");
    }
}
