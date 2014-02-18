package com.autonomy.frontend.configuration;

import com.autonomy.common.io.IOUtils;
import com.autonomy.common.lang.StringUtils;
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
import org.jasypt.util.text.TextEncryptor;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/*
 * $Id$
 *
 * Copyright (c) 2013, Autonomy Systems Ltd.
 *
 * Last modified by $Author$ on $Date$
 */

/**
 * Reference implementation of {@link ConfigFileService}, which outputs configuration objects as JSON files.
 * An additional type bound is placed on the configuration object this class uses.
 *
 * This class requires that a default config file be available at runtime.
 *
 * Operations on the Config are thread safe.
 *
 * @param <T> The type of the Configuration object. If it extends {@link PasswordsConfig}, passwords will be encrypted
 *           and decrypted when the file is written and read respectively.  If it extends {@link LoginConfig}, a default
 *           login will be generated for the initial config file, and which will be removed on subsequent writes.
 */
@Slf4j
public abstract class AbstractConfigFileService<T extends Config<T>> implements ConfigFileService<T>, ResourceLoaderAware {

    private String configFileLocation;
    private String configFileName;

    private String defaultConfigFile;

    // Use AtomicReference for thread safety
    private final AtomicReference<T> config = new AtomicReference<>(null);

	private ResourceLoader resourceLoader;

    private ObjectMapper mapper;

    private TextEncryptor textEncryptor;

    private final Object updateLock = new Object();

    private FilterProvider filterProvider;

    private ValidationService<T> validationService;

    /**
     * Initialises the service.
     *
     * @throws BeanInitializationException If an error occurs which prevent bean initialization
     * @throws Exception If an unspecified error occurs
     */
	// Exception thrown by method in library
    @PostConstruct
    public void init() throws Exception {
        final String configFileLocation = getConfigFileLocation();
        T fileConfig = getEmptyConfig();

        if(StringUtils.isNotBlank(configFileLocation)) {
            AbstractConfigFileService.log.debug("Using {} as config file location", configFileLocation);

            Reader reader = null;

            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(configFileLocation), "UTF-8"));
                fileConfig = mapper.readValue(reader, getConfigClass());

                if(fileConfig instanceof PasswordsConfig<?>) {
                    fileConfig = ((PasswordsConfig<T>) fileConfig).withDecryptedPasswords(textEncryptor);
                }
            }
            catch(FileNotFoundException e) {
                AbstractConfigFileService.log.warn("Config file not found, using empty configuration object");
            }
            catch(IOException e) {
                AbstractConfigFileService.log.error("Error reading config file at {}", configFileLocation);
                AbstractConfigFileService.log.error("Recording stack trace", e);
                // throw this so we don't continue to start the webapp
                throw new BeanInitializationException("Could not initialize configuration", e);
            }
            finally {
                IOUtils.closeQuietly(reader);
            }

            final Resource defaultConfigResource = resourceLoader.getResource(defaultConfigFile);
            InputStream defaultConfigInputStream = null;

            try {
                defaultConfigInputStream = defaultConfigResource.getInputStream();
                final T defaultConfig = mapper.readValue(defaultConfigInputStream, getConfigClass());

                T mergedConfig = fileConfig.merge(defaultConfig);

                if(fileConfig.equals(getEmptyConfig())) {
                    if(mergedConfig instanceof LoginConfig<?>) {
                        mergedConfig = ((LoginConfig<T>) mergedConfig).generateDefaultLogin();
                    }

                    try {
                        writeOutConfigFile(mergedConfig, configFileLocation);
                    }
                    catch(IOException e) {
                        throw new BeanInitializationException("Could not initialize configuration", e);
                    }
                }

                mergedConfig.basicValidate();
                this.config.set(mergedConfig);

                postInitialise(getConfig());
            }
            catch(IOException e) {
                AbstractConfigFileService.log.error("Error reading default config file", e);
                // throw this so we don't continue to start the webapp
                throw new BeanInitializationException("Could not initialize configuration", e);
            }
            catch(ConfigException e){
            	AbstractConfigFileService.log.error("Config validation failed in " + e);
            	throw new BeanInitializationException("Could not initialize configuration", e);
            }
            finally {
                IOUtils.closeQuietly(defaultConfigInputStream);
            }
        }
        else {
            AbstractConfigFileService.log.info("Cannot read value from {}", configFileLocation);
            AbstractConfigFileService.log.debug("Environment is {}", System.getenv());
        }
    }

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

        if(config instanceof LoginConfig<?>) {
            config = ((LoginConfig<T>) config).withoutDefaultLogin();
        }

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

            if(mergedConfig instanceof LoginConfig<?>) {
                mergedConfig = ((LoginConfig<T>) mergedConfig).withoutDefaultLogin();
            }

            this.config.set(mergedConfig);
        }
    }

	private void writeOutConfigFile(final T config, final String configFileLocation) throws IOException {
        Writer writer = null;

        try {
            AbstractConfigFileService.log.debug("Writing out config file to {}", configFileLocation);

            T configToWrite = config;

            if(configToWrite instanceof PasswordsConfig<?>) {
                configToWrite = ((PasswordsConfig<T>) configToWrite).withEncryptedPasswords(textEncryptor);
            }

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFileLocation), "UTF-8"));

            if(filterProvider != null) {
                mapper.addMixInAnnotations(ServerConfig.class, ConfigurationFilterMixin.class);
                mapper.writer(filterProvider).writeValue(writer, configToWrite);
            }
            else {
                mapper.writeValue(writer, configToWrite);
            }
        }
        catch(IOException e) {
            AbstractConfigFileService.log.error("Error writing out config file", e);
            throw e;
        }
        finally {
            IOUtils.closeQuietly(writer);
        }
    }

    @Override
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
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
     * @param defaultConfigFile The path to the default config file.
     *                          Accepts anything accepted by a {@link ResourceLoader}
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
}
