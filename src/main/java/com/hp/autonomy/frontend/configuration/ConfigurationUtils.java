package com.hp.autonomy.frontend.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings({"UtilityClass", "WeakerAccess"})
public class ConfigurationUtils {
    public static <F extends ConfigurationComponent<F>> F mergeConfiguration(final F local, final F defaults, final Supplier<F> merge) {
        return Optional.ofNullable(defaults).map(v -> merge.get()).orElse(local);
    }

    public static <F extends ConfigurationComponent<F>> F mergeConfiguration(final F local, final F defaults, final BiFunction<F, F, F> merge) {
        return Optional.ofNullable(defaults).map(v -> merge.apply(local, v)).orElse(local);
    }

    public static <F> F mergeField(final F localValue, final F defaultValue) {
        return Optional.ofNullable(localValue).orElse(defaultValue);
    }

    public static <F extends ConfigurationComponent<F>> F mergeComponent(final F localValue, final F defaultValue) {
        return Optional.ofNullable(localValue).map(v -> v.merge(defaultValue)).orElse(defaultValue);
    }

    public static <K, V> Map<K, V> mergeMap(final Map<K, V> localValue, final Map<K, V> defaultValue) {
        return Optional.ofNullable(localValue).map(v -> Optional.ofNullable(defaultValue).map(w -> {
            final Map<K, V> map = new HashMap<>(w);
            map.putAll(v);
            return map;
        }).orElse(localValue)).orElse(defaultValue);
    }

    public static <F extends ConfigurationComponent<F>> void basicValidate(final F component, final String section) throws ConfigException {
        // TODO: see about making ConfigException a runtime exception and remove this hack
        try {
            Optional.ofNullable(component).ifPresent(v -> {
                try {
                    v.basicValidate(section);
                } catch (final ConfigException e) {
                    throw new ConfigRuntimeException(e.getMessage(), e);
                }
            });
        } catch (final ConfigRuntimeException e) {
            throw new ConfigException(e.getMessage(), e);
        }
    }

    private static class ConfigRuntimeException extends RuntimeException {
        private static final long serialVersionUID = 3624972600385414973L;

        private ConfigRuntimeException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
