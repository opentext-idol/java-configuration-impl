/*
 * Copyright 2013-2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.hp.autonomy.frontend.configuration.passwords.PasswordsConfig;
import com.hp.autonomy.frontend.configuration.validation.ConfigValidationException;
import com.hp.autonomy.frontend.configuration.validation.ValidationResults;
import com.hp.autonomy.frontend.configuration.validation.ValidationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jasypt.util.text.TextEncryptor;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Reference implementation of {@link ConfigFileService}, which outputs configuration objects as JSON files.
 * An additional type bound is placed on the configuration object this class uses.
 * <p>
 * Clients of this API should extend {@link AbstractAuthenticatingConfigFileService} or
 * {@link AbstractUnauthenticatingConfigFileService}, depending on their needs.
 * <p>
 * This class requires that a default config file be available at runtime.
 * <p>
 * Operations on the Config object are thread safe.
 *
 * @param <T> The type of the Configuration object. If it extends {@link PasswordsConfig}, passwords will be encrypted
 *            and decrypted when the file is written and read respectively.
 */
@Slf4j
public abstract class BaseConfigFileService<T extends Config<T>> implements ConfigFileService<T> {

    private String configFileLocation;
    private String configFileName;

    private String defaultConfigFile;

    // Use AtomicReference for thread safety
    private final AtomicReference<T> config = new AtomicReference<>(null);

    private ObjectMapper mapper;

    private TextEncryptor textEncryptor;

    private final Object updateLock = new Object();

    private FilterProvider filterProvider;

    private ValidationService<T> validationService;

    /**
     * Initialises the service
     *
     * @throws IllegalStateException If an error occurs which prevent service initialization
     * @throws Exception             If an unspecified error occurs
     */
    // Exception thrown by method in library
    @SuppressWarnings("ProhibitedExceptionDeclared")
    @PostConstruct
    public void init() throws Exception {
        final String configFileLocation = getConfigFileLocation();
        T fileConfig = getEmptyConfig();

        boolean fileExists = false;
        if (StringUtils.isNotBlank(configFileLocation)) {
            log.debug("Using {} as config file location", configFileLocation);

            try (Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFileLocation), "UTF-8"))) {
                fileConfig = mapper.readValue(reader, getConfigClass());

                if (fileConfig instanceof PasswordsConfig<?>) {
                    fileConfig = ((PasswordsConfig<T>) fileConfig).withDecryptedPasswords(textEncryptor);
                }

                fileExists = true;
            } catch (final FileNotFoundException e) {
                log.warn("Config file not found, using empty configuration object");
            } catch (final IOException e) {
                log.error("Error reading config file at {}", configFileLocation);
                log.error("Recording stack trace", e);
                // throw this so we don't continue to start the webapp
                throw new IllegalStateException("Could not initialize configuration", e);
            }
        }

        if (StringUtils.isNotBlank(defaultConfigFile)) {
            try (InputStream defaultConfigInputStream = getClass().getResourceAsStream(defaultConfigFile)) {
                if (defaultConfigInputStream != null) {
                    final T defaultConfig = mapper.readValue(defaultConfigInputStream, getConfigClass());

                    fileConfig = fileConfig.merge(defaultConfig);

                    if (!fileExists) {
                        fileConfig = generateDefaultLogin(fileConfig);

                        try {
                            writeOutConfigFile(fileConfig, configFileLocation);
                        } catch (final IOException e) {
                            throw new IllegalStateException("Could not initialize configuration", e);
                        }
                    }
                }
            } catch (final IOException e) {
                log.error("Error reading default config file", e);
                // throw this so we don't continue to start the webapp
                throw new IllegalStateException("Could not initialize configuration", e);
            }
        }

        try {
            fileConfig.basicValidate("Root");
        } catch (final ConfigException e) {
            log.error("Config validation failed in " + e);
            throw new IllegalStateException("Could not initialize configuration", e);
        }

        if (fileConfig.equals(getEmptyConfig())) {
            log.info("Cannot read value from {}", configFileLocation);
            log.debug("Environment is {}", System.getenv());
        } else {
            config.set(fileConfig);
            postInitialise(getConfig());
        }
    }

    protected String getConfigFileLocation() {
        if (configFileLocation != null) {
            final String propertyValue = System.getProperty(configFileLocation);
            if (propertyValue != null) {
                return propertyValue + (propertyValue.matches(".*(?:/|\\\\)$") ? configFileName : File.separator + configFileName);
            }
        }

        throw new IllegalStateException("No configuration file defined. System property key: " + configFileLocation);
    }

    /**
     * @inheritDoc
     */
    @Override
    public T getConfig() {
        return config.get();
    }

    /**
     * Returns the Config in a format suitable for revealing to users.
     *
     * @return A ConfigResponse of the current config. Passwords will be encrypted and the default login wil be removed.
     */
    @Override
    public ConfigResponse<T> getConfigResponse() {
        T config = this.config.get();

        config = withoutDefaultLogin(config);

        if (config instanceof PasswordsConfig<?>) {
            config = ((PasswordsConfig<T>) config).withoutPasswords();
        }

        return new ConfigResponse<>(config, getConfigFileLocation(), configFileLocation);
    }

    /**
     * Validate the config with the supplied {@link ValidationService}, calls {@link #withoutDefaultLogin(Config)}
     * and {@link #withHashedPasswords(Config)} before setting the config, and calls {@link #postUpdate}
     * before writing the config to a file.
     *
     * @param config The new config
     * @throws IOException               If an error occurs writing out the config file
     * @throws ConfigValidationException If the supplied config is invalid.
     * @throws Exception                 if an unspecified error occurs in postUpdate
     */
    @Override
    public void updateConfig(final T config) throws Exception {
        final ValidationResults validationResults = validationService.validateEnabledConfig(config);

        if (!validationResults.isValid()) {
            throw new ConfigValidationException(validationResults);
        }

        final T initialConfig = getConfig();
        setConfig(config);

        safePostUpdate(config, initialConfig);

        try {
            writeOutConfigFile(this.config.get(), getConfigFileLocation());
        } catch (final IOException e) {
            setConfig(initialConfig);
            safePostUpdate(initialConfig, initialConfig);

            throw e;
        }
    }

    private void safePostUpdate(final T config, final T initialConfig) throws Exception {
        try {
            postUpdate(config);
        } catch (final ConfigException ce) {
            setConfig(initialConfig);
            throw ce;
        }
    }

    private void setConfig(final T config) {
        final T preMergeConfig = preUpdate(config);

        synchronized (updateLock) {
            T mergedConfig = preMergeConfig.merge(this.config.get());

            mergedConfig = withoutDefaultLogin(mergedConfig);
            mergedConfig = withHashedPasswords(mergedConfig);

            this.config.set(mergedConfig);
        }
    }

    private void writeOutConfigFile(final T config, final String configFileLocation) throws IOException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFileLocation), "UTF-8"))) {
            log.debug("Writing out config file to {}", configFileLocation);

            T configToWrite = config;

            if (configToWrite instanceof PasswordsConfig<?>) {
                configToWrite = ((PasswordsConfig<T>) configToWrite).withEncryptedPasswords(textEncryptor);
            }

            // TODO: This scales badly and needs rethinking
            if (filterProvider != null) {
                mapper.writer(filterProvider).writeValue(writer, configToWrite);
            } else {
                mapper.writeValue(writer, configToWrite);
            }
        } catch (final IOException e) {
            log.error("Error writing out config file", e);
            throw e;
        }
    }

    /**
     * @param systemProperty The name of the system property which stores the location
     *                       of the config file.
     */
    public void setConfigFileLocation(final String systemProperty) {
        configFileLocation = systemProperty;
    }

    /**
     * @param configFileName The name of the config file
     */
    public void setConfigFileName(final String configFileName) {
        this.configFileName = configFileName;
    }

    /**
     * @param defaultConfigFile The path to the default config file. This should be a resource on the classpath e.g
     *                          com/example/foo/defaultConfigFile.json
     */
    public void setDefaultConfigFile(final String defaultConfigFile) {
        this.defaultConfigFile = defaultConfigFile;
    }

    /**
     * @param mapper The {@link ObjectMapper} used to perform JSON conversion.
     */
    public void setMapper(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * If {@link T} is not a {@link PasswordsConfig}, this property need not be set.
     *
     * @param textEncryptor The {@link TextEncryptor} used to encrypt passwords.
     */
    public void setTextEncryptor(final TextEncryptor textEncryptor) {
        this.textEncryptor = textEncryptor;
    }

    /**
     * This property is optional if no filtering is required.
     *
     * @param filterProvider The {@link FilterProvider} used to filter the config before writing. This must provide a
     *                       filter called "configurationFilter".
     */
    public void setFilterProvider(final FilterProvider filterProvider) {
        this.filterProvider = filterProvider;
    }

    /**
     * @param validationService The {@link ValidationService} used to perform validation
     */
    public void setValidationService(final ValidationService<T> validationService) {
        this.validationService = validationService;
    }

    /**
     * Called after the Config is initialised
     *
     * @param config The newly initialised config
     * @throws Exception
     */
    public abstract void postInitialise(final T config) throws Exception;

    /**
     * @return The class object representing T.
     */
    public abstract Class<T> getConfigClass();

    /**
     * Returns a configuration object on which no properties have been set.
     *
     * @return An empty configuration object.
     */
    public abstract T getEmptyConfig();

    /**
     * Generates a default login for a new config file
     *
     * @param config The initial config object
     * @return A copy of config with a default login, or the same config object if a default login is not required
     */
    public abstract T generateDefaultLogin(T config);

    /**
     * Removes the default login from the configuration object
     *
     * @param config The initial config object
     * @return A copy of config without a default login, or the same config object if a default login is not required
     */
    public abstract T withoutDefaultLogin(T config);

    /**
     * Hashes any passwords in the configuration object
     *
     * @param config The initial config object
     * @return A copy of config without any plaintext passwords, or the same config object if there are no passwords
     */
    public abstract T withHashedPasswords(T config);
}
