package com.hp.autonomy.frontend.configuration.test;

import com.hp.autonomy.frontend.configuration.Config;
import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.validation.OptionalConfigurationComponent;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

@SuppressWarnings("unused")
public class SampleConfig implements Config<SampleConfig> {
    private String someField;
    private String someNewField;
    private SomeObject someObject;

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
    }

    @Override
    public SampleConfig merge(final SampleConfig other) {
        someField = StringUtils.defaultIfEmpty(someField, other.someField);
        someNewField = StringUtils.defaultIfEmpty(someNewField, other.someNewField);
        if (someObject == null) {
            someObject = new SomeObject();
        }
        someObject.someNestedField = StringUtils.defaultIfEmpty(someObject.someNestedField, other.someObject.someNestedField);

        return this;
    }

    public String getSomeField() {
        return someField;
    }

    public void setSomeField(final String someField) {
        this.someField = someField;
    }

    public String getSomeNewField() {
        return someNewField;
    }

    public void setSomeNewField(final String someNewField) {
        this.someNewField = someNewField;
    }

    public SomeObject getSomeObject() {
        return someObject;
    }

    public void setSomeObject(final SomeObject someObject) {
        this.someObject = someObject;
    }

    @SuppressWarnings({"InnerClassTooDeeplyNested", "WeakerAccess"})
    public static class SomeObject {
        private String someNestedField;

        public String getSomeNestedField() {
            return someNestedField;
        }

        public void setSomeNestedField(final String someNestedField) {
            this.someNestedField = someNestedField;
        }
    }
}
