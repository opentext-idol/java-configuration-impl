package com.hp.autonomy.frontend.configuration.test;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.Config;
import com.hp.autonomy.frontend.configuration.SimpleComponent;
import com.hp.autonomy.frontend.configuration.validation.OptionalConfigurationComponent;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@SuppressWarnings("unused")
@Getter
@Builder
@JsonDeserialize(builder = SampleConfig.SampleConfigBuilder.class)
public class SampleConfig extends SimpleComponent<SampleConfig> implements Config<SampleConfig> {
    private String someField;
    private String someNewField;
    private SomeComponent someComponent;

    @Override
    public Map<String, OptionalConfigurationComponent<?>> getValidationMap() {
        return null;
    }

    @Override
    public Map<String, OptionalConfigurationComponent<?>> getEnabledValidationMap() {
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @JsonPOJOBuilder(withPrefix = "")
    public static class SampleConfigBuilder {
    }

    @SuppressWarnings({"InnerClassTooDeeplyNested", "WeakerAccess"})
    @Getter
    @Builder
    @JsonDeserialize(builder = SomeComponent.SomeComponentBuilder.class)
    public static class SomeComponent extends SimpleComponent<SomeComponent> {
        private String someNestedField;

        @SuppressWarnings("WeakerAccess")
        @JsonPOJOBuilder(withPrefix = "")
        public static class SomeComponentBuilder {
        }
    }
}
