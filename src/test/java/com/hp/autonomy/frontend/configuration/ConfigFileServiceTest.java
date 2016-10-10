package com.hp.autonomy.frontend.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.autonomy.frontend.configuration.test.SampleConfig;
import com.hp.autonomy.frontend.configuration.test.SampleConfigFileService;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

@SuppressWarnings("ProhibitedExceptionDeclared")
public class ConfigFileServiceTest {
    private static final String TEST_DIR = "./target/test";
    private static final String CONFIG_FILE_SYSTEM_PROPERTY = "some.property";
    private static final String CONFIG_FILE_NAME = "sampleConfig.json";
    private static final String DEFAULT_CONFIG_FILE_NAME = "/defaultSampleConfig.json";

    @BeforeClass
    public static void init() throws IOException {
        System.setProperty(CONFIG_FILE_SYSTEM_PROPERTY, TEST_DIR);
        final File directory = new File(TEST_DIR);
        FileUtils.forceMkdir(directory);
        FileUtils.copyFileToDirectory(new File("./src/test/resources", CONFIG_FILE_NAME), directory);
    }

    @AfterClass
    public static void destroy() throws IOException {
        FileUtils.forceDelete(new File(TEST_DIR));
    }

    private SampleConfigFileService configFileService;

    @Before
    public void setUp() throws Exception {
        configFileService = new SampleConfigFileService();

        System.setProperty(CONFIG_FILE_SYSTEM_PROPERTY, TEST_DIR);
        configFileService.setConfigFileLocation(CONFIG_FILE_SYSTEM_PROPERTY);
        configFileService.setConfigFileName(CONFIG_FILE_NAME);
        configFileService.setDefaultConfigFile(DEFAULT_CONFIG_FILE_NAME);
        configFileService.setMapper(new ObjectMapper());
    }

    @Test
    public void undefinedDefaultFile() throws Exception {
        configFileService.setDefaultConfigFile(null);
        configFileService.init();
        noDefaultValidation();
    }

    @Test
    public void noDefaultFile() throws Exception {
        configFileService.setDefaultConfigFile("/bad");
        configFileService.init();
        noDefaultValidation();
    }

    private void noDefaultValidation() {
        final SampleConfig sampleConfig = configFileService.getConfig();
        assertNotNull(sampleConfig);
        assertNull(sampleConfig.getSomeField());
        assertNotNull(sampleConfig.getSomeNewField());
    }

    @Test(expected = IllegalStateException.class)
    public void undefinedConfigFile() throws Exception {
        configFileService.setConfigFileName(null);
        configFileService.setConfigFileLocation(null);
        configFileService.init();
        configFileService.getConfig();
    }

    @Test
    public void noConfigFile() throws Exception {
        final String nonExistentFileName = "nonExistent.json";
        configFileService.setConfigFileName(nonExistentFileName);
        configFileService.init();
        final SampleConfig sampleConfig = configFileService.getConfig();
        assertNotNull(sampleConfig);
        assertNotNull(sampleConfig.getSomeField());
        assertNull(sampleConfig.getSomeNewField());
        assertTrue(new File(TEST_DIR, nonExistentFileName).exists());
    }

    @Test
    public void configMerge() throws Exception {
        configFileService.init();
        final SampleConfig sampleConfig = configFileService.getConfig();
        assertNotNull(sampleConfig);
        assertNotNull(sampleConfig.getSomeField());
        assertNotNull(sampleConfig.getSomeNewField());
        assertNotNull(sampleConfig.getSomeObject());
        assertEquals("y", sampleConfig.getSomeObject().getSomeNestedField());
    }
}
