package com.hp.autonomy.frontend.configuration;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Utilities for common configuration component operations
 */
@SuppressWarnings({"UtilityClass", "WeakerAccess"})
public class ConfigurationUtils {
    private static final String STATIC_BUILDER_FACTORY_METHOD = "builder";
    private static final String BUILD_METHOD = "build";

    /**
     * Merges (nullable) local configuration with (nullable) default configuration for a given custom merge function
     *
     * @param local    local configuration object
     * @param defaults default configuration object
     * @param merge    the merge function
     * @param <F>      the configuration object type
     * @return the merged configuration
     */
    public static <F extends ConfigurationComponent<F>> F mergeConfiguration(final F local, final F defaults, final Supplier<F> merge) {
        return Optional.ofNullable(defaults).map(v -> merge.get()).orElse(local);
    }

    /**
     * Merges a (nullable) simple field on the local config object with a default (nullable) value in the default config object
     *
     * @param localValue   local field value
     * @param defaultValue default field value
     * @param <F>          the configuration object type
     * @return the merged field value
     */
    public static <F> F mergeField(final F localValue, final F defaultValue) {
        return Optional.ofNullable(localValue).orElse(defaultValue);
    }

    /**
     * Merges a (nullable) object field on the local config object with a default (nullable) value in the default config object
     *
     * @param localValue   local field value
     * @param defaultValue default field value
     * @param <F>          the configuration object type
     * @return the merged field value
     */
    public static <F extends ConfigurationComponent<F>> F mergeComponent(final F localValue, final F defaultValue) {
        return Optional.ofNullable(localValue).map(v -> v.merge(defaultValue)).orElse(defaultValue);
    }

    /**
     * Merges a (nullable) map on the local config object with a (nullable) map in the default config object
     * by adding all the fields from the default map to a new map, and then adding the local fields on top
     *
     * @param localValue   local field value
     * @param defaultValue default field value
     * @param <K>          the map key type
     * @param <V>          the map value type
     * @return the merged field value
     */
    public static <K, V> Map<K, V> mergeMap(final Map<K, V> localValue, final Map<K, V> defaultValue) {
        return Optional.ofNullable(localValue).map(v -> Optional.ofNullable(defaultValue).map(w -> {
            final Map<K, V> map = new LinkedHashMap<>(w);
            map.putAll(v);
            return map;
        }).orElse(localValue)).orElse(defaultValue);
    }

    /**
     * Calls {@link ConfigurationComponent#basicValidate(String)} on the supplied component if not null
     *
     * @param component the nullable component
     * @param section   the configuration section
     * @param <F>       the component type
     * @throws ConfigException validation failure
     */
    public static <F extends ConfigurationComponent<F>> void basicValidate(final F component, final String section) throws ConfigException {
        final Optional<F> maybeComponent = Optional.ofNullable(component);
        if (maybeComponent.isPresent()) {
            maybeComponent.get().basicValidate(section);
        }
    }

    /**
     * Performs skeleton validation, searching for any non-null {@link ConfigurationComponent} fields and calling {@link ConfigurationComponent#basicValidate(String)}
     *
     * @param local    local configuration object
     * @param defaults default configuration object
     * @param <F>      the configuration object type
     * @return the merged configuration
     * @see SimpleComponent for basic usage
     */
    public static <F extends ConfigurationComponent<F>> F defaultMerge(final F local, final F defaults) {
        return mergeConfiguration(local, defaults, () -> defaultMergeInternal(local, defaults));
    }

    /**
     * Calls {@link ConfigurationComponent#basicValidate(String)} on the supplied component if not null
     *
     * @param component the nullable component
     * @param section   the configuration section
     * @param <F>       the component type
     * @throws ConfigException validation failure
     * @see SimpleComponent for basic usage
     */
    public static <F extends ConfigurationComponent<F>> void defaultValidate(final F component, final String section) throws ConfigException {
        try {
            final Class<?> type = component.getClass();
            final BeanInfo beanInfo = Introspector.getBeanInfo(type);
            for (final PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                if (ConfigurationComponent.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
                    @SuppressWarnings("rawtypes")
                    final ConfigurationComponent subComponent = (ConfigurationComponent) propertyDescriptor.getReadMethod().invoke(component);
                    basicValidate(subComponent, section);
                }
            }
        } catch (final IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            throw new ConfigException("Error performing config bean introspection", e);
        }
    }

    private static <F extends ConfigurationComponent<F>> F defaultMergeInternal(final F local, final F defaults) {
        try {
            final Class<?> type = local.getClass();
            final Object builder = type.getMethod(STATIC_BUILDER_FACTORY_METHOD).invoke(null);
            final Class<?> builderType = builder.getClass();
            final BeanInfo beanInfo = Introspector.getBeanInfo(type);
            for (final PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                final String propertyName = propertyDescriptor.getName();
                final Class<?> propertyType = propertyDescriptor.getPropertyType();
                final Method getter = propertyDescriptor.getReadMethod();
                final Object localValue = getter.invoke(local);
                final Object defaultValue = getter.invoke(defaults);
                @SuppressWarnings({"unchecked", "rawtypes"})
                final Object mergedValue = mergeProperty((Class) propertyType, localValue, defaultValue);
                final Optional<Method> maybeSetter = getMethod(builderType, propertyName, propertyType);
                if (maybeSetter.isPresent()) {
                    maybeSetter.get().invoke(builder, mergedValue);
                }
            }

            @SuppressWarnings("unchecked")
            final F mergedComponent = (F) builderType.getMethod(BUILD_METHOD).invoke(builder);
            return mergedComponent;
        } catch (final IllegalAccessException | InvocationTargetException | IntrospectionException | NoSuchMethodException e) {
            throw new ConfigRuntimeException("Error performing config bean introspection", e);
        }
    }

    private static <F> F mergeProperty(final Class<F> type, final F localValue, final F defaultValue) {
        final BiFunction<F, F, F> mergeFunction = getMergeFunctionForField(type);
        return mergeFunction.apply(localValue, defaultValue);
    }

    @SuppressWarnings({"unchecked", "rawtypes", "RedundantCast"})
    private static <F> BiFunction<F, F, F> getMergeFunctionForField(final Class<F> type) {
        final BiFunction<F, F, F> mergeFunction;
        if (ConfigurationComponent.class.isAssignableFrom(type)) {
            mergeFunction = (BiFunction) (local, defaults) -> mergeComponent((ConfigurationComponent) local, (ConfigurationComponent) defaults);
        } else if (Map.class.isAssignableFrom(type)) {
            mergeFunction = (BiFunction) (local, defaults) -> mergeMap((Map) local, (Map) defaults);
        } else {
            mergeFunction = ConfigurationUtils::mergeField;
        }

        return mergeFunction;
    }

    private static Optional<Method> getMethod(final Class<?> builderType, final String methodName, final Class<?>... parameterTypes) {
        Optional<Method> maybeMethod;
        try {
            maybeMethod = Optional.of(builderType.getMethod(methodName, parameterTypes));
        } catch (final NoSuchMethodException ignored) {
            maybeMethod = Optional.empty();
        }

        return maybeMethod;
    }

    static class ConfigRuntimeException extends RuntimeException {
        private static final long serialVersionUID = 3624972600385414973L;

        private ConfigRuntimeException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
