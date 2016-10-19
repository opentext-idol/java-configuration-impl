package com.hp.autonomy.frontend.configuration;

import lombok.Builder;

/**
 * Provides default implementations for merge and validate.
 * IMPORTANT: classes extending this type must satisfy the contract of the lombok {@link Builder} annotation
 *
 * @param <C> the configuration component type
 */
public abstract class SimpleComponent<C extends SimpleComponent<C>> implements ConfigurationComponent<C> {
    /**
     * Performs a default merge operation between current bean instance and defaults instance
     *
     * @param other The configuration to merge with.
     * @return the merged configuration
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public C merge(final C other) {
        return (C) ConfigurationUtils.defaultMerge((ConfigurationComponent) this, (ConfigurationComponent) other);
    }

    /**
     * Skeleton validation, triggering validate on any subComponents
     *
     * @param section the configuration section; should often be hard-coded
     * @throws ConfigException validation failure
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void basicValidate(final String section) throws ConfigException {
        ConfigurationUtils.defaultValidate((ConfigurationComponent) this, section);
    }
}
