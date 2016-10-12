package com.hp.autonomy.frontend.configuration.redis;

import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.ConfigurationComponentTest;
import com.hp.autonomy.frontend.configuration.server.HostAndPort;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;

public class RedisConfigTest extends ConfigurationComponentTest<RedisConfig> {
    @Test(expected = ConfigException.class)
    public void badAddress() throws ConfigException {
        RedisConfig.builder()
                .address(new HostAndPort(null, null))
                .build()
                .basicValidate(null);
    }

    @Test(expected = ConfigException.class)
    public void noAddress() throws ConfigException {
        RedisConfig.builder()
                .build()
                .basicValidate(null);
    }

    @Test(expected = ConfigException.class)
    public void badSentinel() throws ConfigException {
        RedisConfig.builder()
                .sentinel(new HostAndPort(null, null))
                .build()
                .basicValidate(null);
    }

    @Test(expected = ConfigException.class)
    public void noMaster() throws ConfigException {
        RedisConfig.builder()
                .sentinel(new HostAndPort("host", 2345))
                .build()
                .basicValidate(null);
    }

    @Override
    protected Class<RedisConfig> getType() {
        return RedisConfig.class;
    }

    @Override
    protected RedisConfig constructComponent() {
        return RedisConfig.builder()
                .masterName("OVERLORD")
                .sentinel(new HostAndPort("example.com", 8080))
                .build();
    }

    @Override
    protected String sampleJson() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/frontend/configuration/redis/redis.json"));
    }

    @Override
    protected void validateJson(final JsonContent<RedisConfig> json) {
        json.assertThat().hasJsonPathStringValue("@.masterName", "OVERLORD");
        json.assertThat().hasJsonPathArrayValue("@.sentinels", new HostAndPort("example.com", 8080));
    }

    @Override
    protected void validateParsedComponent(final ObjectContent<RedisConfig> component) {
        component.assertThat().hasFieldOrPropertyWithValue("masterName", "MASTER");
        component.assertThat().hasFieldOrPropertyWithValue("address", new HostAndPort("localhost", 1234));
    }

    @Override
    protected void validateMergedComponent(final ObjectContent<RedisConfig> mergedComponent) {
        assertNotNull(mergedComponent.getObject().getAddress());
        assertThat(mergedComponent.getObject().getSentinels(), hasSize(1));
    }
}
