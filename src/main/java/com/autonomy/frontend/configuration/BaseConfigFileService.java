package com.autonomy.frontend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
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
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jasypt.util.text.TextEncryptor;

/*
 * $Id$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author$ on $Date$
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
     * Initialises the service.
     *
     * @throws IllegalStateException If an error occurs which prevent bean initialization
     * @throws Exception If an unspecified error occurs
     */
	// Exception thrown by method in library
    @PostConstruct
    public void init() throws Exception {
        final String configFileLocation = getConfigFileLocation();
        T fileConfig = getEmptyConfig();

        if(StringUtils.isNotBlank(configFileLocation)) {
            log.debug("Using {} as config file location", configFileLocation);

            try(Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFileLocation), "UTF-8"))){
                addPreReadMixins(mapper);
                fileConfig = mapper.readValue(reader, getConfigClass());

                if(fileConfig instanceof PasswordsConfig<?>) {
                    fileConfig = ((PasswordsConfig<T>) fileConfig).withDecryptedPasswords(textEncryptor);
                }
            }
            catch(FileNotFoundException e) {
                log.warn("Config file not found, using empty configuration object");
            }
            catch(IOException e) {
                log.error("Error reading config file at {}", configFileLocation);
                log.error("Recording stack trace", e);
                // throw this so we don't continue to start the webapp
                throw new IllegalStateException("Could not initialize configuration", e);
            }

            try (InputStream defaultConfigInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(defaultConfigFile)){
                final T defaultConfig = mapper.readValue(defaultConfigInputStream, getConfigClass());

                T mergedConfig = fileConfig.merge(defaultConfig);

                if(fileConfig.equals(getEmptyConfig())) {
                    mergedConfig = generateDefaultLogin(mergedConfig);

                    try {
                        writeOutConfigFile(mergedConfig, configFileLocation);
                    }
                    catch(IOException e) {
                        throw new IllegalStateException("Could not initialize configuration", e);
                    }
                }

                mergedConfig.basicValidate();
                this.config.set(mergedConfig);

                postInitialise(getConfig());
            }
            catch(IOException e) {
                log.error("Error reading default config file", e);
                // throw this so we don't continue to start the webapp
                throw new IllegalStateException("Could not initialize configuration", e);
            }
            catch(ConfigException e){
            	log.error("Config validation failed in " + e);
            	throw new IllegalStateException("Could not initialize configuration", e);
            }
        }
        else {
            log.info("Cannot read value from {}", configFileLocation);
            log.debug("Environment is {}", System.getenv());
        }
    }

    /**
     * Add mixins to the ObjectMapper prior to reading in the config file.
     * In most cases this method can be left empty.
     * @param mapper The ObjectMapper to add the mixins to
     */
    protected abstract void addPreReadMixins(ObjectMapper mapper);

    private String getConfigFileLocation() {
        final String propertyValue = System.getProperty(configFileLocation);

        if(propertyValue != null) {
            return propertyValue + File.separator + configFileName;
        } else {
            return null;
        }
    }

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

        if(config instanceof PasswordsConfig<?>) {
            config = ((PasswordsConfig<T>)config).withoutPasswords();
        }

        return new ConfigResponse<>(config, getConfigFileLocation(), configFileLocation);
	}

    /**
     * Validate the config with the supplied {@link ValidationService} and calls {@link #postUpdate(Config)}
     * before writing the config to a file.
     *
     * @param config The new config
     * @throws IOException If an error occurs writing out the config file
     * @throws ConfigValidationException If the supplied config is invalid.
     * @throws Exception if an unspecified error occurs in postUpdate
     */
    @Override
    public void updateConfig(final T config) throws Exception {
        final ValidationResults validationResults = validationService.validateEnabledConfig(config);

        if(!validationResults.isValid()) {
            throw new ConfigValidationException(validationResults);
        }

        final T initialConfig = getConfig();
        setConfig(config);

        try{
            postUpdate(config);
        } catch (ConfigException ce) {
            setConfig(initialConfig);
            throw ce;
        }

        writeOutConfigFile(this.config.get(), getConfigFileLocation());
    }

    private void setConfig(final T config) {
        final T preMergeConfig = preUpdate(config);

        synchronized(updateLock) {
            T mergedConfig = preMergeConfig.merge(this.config.get());

            mergedConfig = withoutDefaultLogin(mergedConfig);
            mergedConfig = withHashedPasswords(mergedConfig);

            this.config.set(mergedConfig);
        }
    }

	private void writeOutConfigFile(final T config, final String configFileLocation) throws IOException {
        try(Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFileLocation), "UTF-8"))){
            log.debug("Writing out config file to {}", configFileLocation);

            T configToWrite = config;

            if(configToWrite instanceof PasswordsConfig<?>) {
                configToWrite = ((PasswordsConfig<T>) configToWrite).withEncryptedPasswords(textEncryptor);
            }

            // TODO: This scales badly and needs rethinking
            if(filterProvider != null) {
                mapper.writer(filterProvider).writeValue(writer, configToWrite);
            }
            else {
                mapper.writeValue(writer, configToWrite);
            }
        }
        catch(IOException e) {
            log.error("Error writing out config file", e);
            throw e;
        }
    }

    /**
     * @param systemProperty The name of the system property which stores the location
     *                            of the config file.
     */
    public void setConfigFileLocation(final String systemProperty) {
        this.configFileLocation = systemProperty;
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
     *
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
     * @param textEncryptor The {@link TextEncryptor} used to encrypt passwords.
     */
    public void setTextEncryptor(final TextEncryptor textEncryptor) {
        this.textEncryptor = textEncryptor;
    }

    /**
     * This property is optional if no filtering is required.
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
     * Called before the config is updated to allow properties to be added or removed. This method can be implemented
     * as an identity method.
     *
     * @param config The config to be modified.
     * @return A new config where the changes have been applied.
     */
    public abstract T preUpdate(final T config);

    public abstract void postUpdate(final T config) throws Exception;

    /**
     * Called after the Config is initialised
     * @param config The newly initialised config
     * @throws Exception
     */
    public abstract void postInitialise(final T config) throws Exception;

    public abstract Class<T> getConfigClass();

    /**
     * Returns a configuration object on which no properties have been set.
     *
     * @return An empty configuration object.
     */
    public abstract T getEmptyConfig();

    public abstract T generateDefaultLogin(T config);

    public abstract T withoutDefaultLogin(T config);

    public abstract T withHashedPasswords(T config);
}
