package com.hp.autonomy.frontend.configuration.authentication;

import com.hp.autonomy.frontend.configuration.ConfigException;
import com.hp.autonomy.frontend.configuration.ConfigurationComponentTest;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.boot.test.json.ObjectContent;

import java.io.IOException;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class BCryptUsernameAndPasswordTest extends ConfigurationComponentTest<BCryptUsernameAndPassword> {
    @Test(expected = ConfigException.class)
    public void noUsername() throws ConfigException {
        BCryptUsernameAndPassword.builder().build().basicValidate(null);
    }

    @Test
    public void isEnabled() {
        assertTrue(constructComponent().isEnabled());
    }

    @Ignore //TODO: not obvious how this is meant to work
    @Test
    public void withHashedPassword() {
        final BCryptUsernameAndPassword component = BCryptUsernameAndPassword.builder()
                .username("Sinbad the Sailor")
                .currentPassword("Open Sesame")
                .plaintextPassword("Open Sesame")
                .build();
        final BCryptUsernameAndPassword withHashedPassword = component.withHashedPassword();
        assertNull(withHashedPassword.getCurrentPassword());
        assertNull(withHashedPassword.getPlaintextPassword());
        assertNotNull(withHashedPassword.getHashedPassword());
        assertTrue(withHashedPassword.validate(component, new UsernameAndPassword("Sinbad the Sailor", "Open Sesame")).isValid());
    }

    @Test
    public void withoutPasswords() {
        final BCryptUsernameAndPassword component = BCryptUsernameAndPassword.builder()
                .username("Sinbad the Sailor")
                .currentPassword("Open Sesame")
                .plaintextPassword("Open Sesame")
                .hashedPassword("XYZ")
                .build();
        final BCryptUsernameAndPassword withoutPasswords = component.withoutPasswords();
        assertNull(withoutPasswords.getPlaintextPassword());
        assertNull(withoutPasswords.getHashedPassword());
        Assert.assertTrue(withoutPasswords.isPasswordRedacted());
        assertTrue(withoutPasswords.validate(component, null).isValid());
    }

    @Override
    protected Class<BCryptUsernameAndPassword> getType() {
        return BCryptUsernameAndPassword.class;
    }

    @Override
    protected BCryptUsernameAndPassword constructComponent() {
        return BCryptUsernameAndPassword.builder()
                .username("Sinbad the Sailor")
                .hashedPassword("XYZ")
                .passwordRedacted(false)
                .build();
    }

    @Override
    protected String sampleJson() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/frontend/configuration/authentication/bcryptUsernameAndPassword.json"));
    }

    @Override
    protected void validateJson(final JsonContent<BCryptUsernameAndPassword> json) {
        json.assertThat().extractingJsonPathStringValue("@.username")
                .isEqualTo("Sinbad the Sailor");
        json.assertThat().extractingJsonPathStringValue("@.hashedPassword")
                .isEqualTo("XYZ");
        json.assertThat().extractingJsonPathBooleanValue("@.passwordRedacted")
                .isEqualTo(false);
    }

    @Override
    protected void validateParsedComponent(final ObjectContent<BCryptUsernameAndPassword> component) {
        component.assertThat().hasFieldOrPropertyWithValue("username", "Sinbad the Sailor");
        component.assertThat().hasFieldOrPropertyWithValue("currentPassword", "Open Sesame");
        component.assertThat().hasFieldOrPropertyWithValue("plaintextPassword", "Open Sesame");
    }

    @Override
    protected void validateMergedComponent(final ObjectContent<BCryptUsernameAndPassword> mergedComponent) {
        mergedComponent.assertThat().hasFieldOrPropertyWithValue("username", "Sinbad the Sailor");
        mergedComponent.assertThat().hasFieldOrPropertyWithValue("currentPassword", "Open Sesame");
        mergedComponent.assertThat().hasFieldOrPropertyWithValue("plaintextPassword", "Open Sesame");
        mergedComponent.assertThat().hasFieldOrPropertyWithValue("hashedPassword", "XYZ");
        mergedComponent.assertThat().hasFieldOrPropertyWithValue("passwordRedacted", false);
    }
}