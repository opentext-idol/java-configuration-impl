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
 *
 * @deprecated Use {@link AbstractAuthenticatingConfigFileService} instead
 */
@Slf4j
@Deprecated
public abstract class AbstractConfigFileService<T extends Config<T>> extends BaseConfigFileService<T> {

    @Override
    T withHashedPasswords(final T config) {
        if(config instanceof LoginConfig<?>) {
            return ((LoginConfig<T>) config).withHashedPasswords();
        }

        return config;
    }

    @Override
    T withoutDefaultLogin(final T config) {
        if(config instanceof LoginConfig<?>) {
            return ((LoginConfig<T>) config).withoutDefaultLogin();
        }

        return config;
    }

    @Override
    T generateDefaultLogin(final T config) {
        if(config instanceof LoginConfig<?>) {
            return ((LoginConfig<T>) config).generateDefaultLogin();
        }

        return config;
    }
}
