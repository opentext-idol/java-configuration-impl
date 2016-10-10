package com.hp.autonomy.frontend.configuration.test;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.hp.autonomy.frontend.configuration.Config;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.ConfigurationComponent;
import com.hp.autonomy.frontend.configuration.ConfigurationUtils;
import com.hp.autonomy.frontend.configuration.validation.OptionalConfigurationComponent;
import lombok.Getter;
import lombok.experimental.Builder;

import java.util.Map;

@SuppressWarnings("unused")
@Getter
@Builder
@JsonDeserialize(builder = SampleConfig.SampleConfigBuilder.class)
public class SampleConfig implements Config<SampleConfig> {
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

    @Override
    public void basicValidate(final String section) throws ConfigException {
        ConfigurationUtils.basicValidate(someComponent, section);
    }

    @Override
    public SampleConfig merge(final SampleConfig other) {
        return ConfigurationUtils.mergeConfiguration(this, other, () -> {
            return builder()
                    .someField(ConfigurationUtils.mergeField(someField, other.someField))
                    .someNewField(ConfigurationUtils.mergeField(someNewField, other.someNewField))
                    .someComponent(ConfigurationUtils.mergeComponent(someComponent, other.someComponent))
                    .build();
        });
    }

    @SuppressWarnings("WeakerAccess")
    @JsonPOJOBuilder(withPrefix = "")
    public static class SampleConfigBuilder {
    }

    @SuppressWarnings({"InnerClassTooDeeplyNested", "WeakerAccess"})
    @Getter
    @Builder
    @JsonDeserialize(builder = SomeComponent.SomeComponentBuilder.class)
    public static class SomeComponent implements ConfigurationComponent<SomeComponent> {
        private String someNestedField;

        @Override
        public SomeComponent merge(final SomeComponent other) {
            return ConfigurationUtils.mergeConfiguration(this, other, () -> {
                return builder()
                        .someNestedField(ConfigurationUtils.mergeField(someNestedField, other.someNestedField))
                        .build();
            });
        }

        @Override
        public void basicValidate(final String section) throws ConfigException {

        }

        @SuppressWarnings("WeakerAccess")
        @JsonPOJOBuilder(withPrefix = "")
        public static class SomeComponentBuilder {
        }
    }
}
