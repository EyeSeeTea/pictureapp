package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SettingsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void should_return_es_if_system_language_es_and_current_language_is_null() {
        String systemLanguage = "es";
        String currentLanguage = null;

        Settings settings = new Settings(systemLanguage, currentLanguage);

        assertThat(settings.getLanguage(), is(systemLanguage));
    }

    @Test
    public void should_return_es_if_system_language_es_and_current_language_is_empty() {
        String systemLanguage = "es";
        String currentLanguage = "";

        Settings settings = new Settings(systemLanguage, currentLanguage);

        assertThat(settings.getLanguage(), is(systemLanguage));
    }

    @Test
    public void should_return_sw_if_current_language_was_changed_to_sw() {
        String systemLanguage = "es";
        String currentLanguage = "sw";

        Settings settings = new Settings(systemLanguage, currentLanguage);

        assertThat(settings.getLanguage(), is(currentLanguage));
    }

    @Test
    public void should_throw_exception_if_system_language_is_null() {
        String systemLanguage = null;
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage);
    }

    @Test
    public void should_throw_exception_if_system_language_is_empty() {
        String systemLanguage = "";
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage);
    }

}
