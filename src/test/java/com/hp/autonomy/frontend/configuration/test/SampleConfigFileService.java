package com.hp.autonomy.frontend.configuration.test;

import com.hp.autonomy.frontend.configuration.BaseConfigFileService;

public class SampleConfigFileService extends BaseConfigFileService<SampleConfig> {
    @Override
    public void postInitialise(final SampleConfig config) {

    }

    @Override
    public Class<SampleConfig> getConfigClass() {
        return SampleConfig.class;
    }

    @Override
    public SampleConfig getEmptyConfig() {
        return new SampleConfig();
    }

    @Override
    public SampleConfig generateDefaultLogin(final SampleConfig config) {
        return config;
    }

    @Override
    public SampleConfig withoutDefaultLogin(final SampleConfig config) {
        return config;
    }

    @Override
    public SampleConfig withHashedPasswords(final SampleConfig config) {
        return config;
    }

    @Override
    public SampleConfig preUpdate(final SampleConfig sampleConfig) {
        return sampleConfig;
    }

    @Override
    public void postUpdate(final SampleConfig sampleConfig) {

    }
}
