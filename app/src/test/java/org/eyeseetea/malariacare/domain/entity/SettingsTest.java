package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
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

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false);

        assertThat(settings.getLanguage(), is(systemLanguage));
    }

    @Test
    public void should_return_es_if_system_language_es_and_current_language_is_empty() {
        String systemLanguage = "es";
        String currentLanguage = "";

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false);

        assertThat(settings.getLanguage(), is(systemLanguage));
    }

    @Test
    public void should_return_sw_if_current_language_was_changed_to_sw() {
        String systemLanguage = "es";
        String currentLanguage = "sw";

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false);

        assertThat(settings.getLanguage(), is(currentLanguage));
    }

    @Test
    public void should_throw_exception_if_system_language_is_null() {
        String systemLanguage = null;
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage, null, false, false);
    }

    @Test
    public void should_throw_exception_if_system_language_is_empty() {
        String systemLanguage = "";
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage, null, false, false);
    }

    @Test
    public void should_return_canDownloadWith3G_true_if_canDownloadWith3G_is_true() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean canDownloadWith3G = true;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, canDownloadWith3G, false);

        assertThat(settings.canDownloadWith3G(), is(canDownloadWith3G));
    }

    @Test
    public void should_return_canDownloadWith3G_false_if_canDownloadWith3G_is_false() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean canDownloadWith3G = false;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, canDownloadWith3G, false);

        assertThat(settings.canDownloadWith3G(), is(canDownloadWith3G));
    }

    @Test
    public void should_return_isElementActive_true_if_isElementActive_is_true() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean isElementActive = true;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, isElementActive);

        assertThat(settings.isElementActive(), is(isElementActive));
    }

    @Test
    public void should_return_isElementActive_false_if_isElementActive_is_false() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean isElementActive = false;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, isElementActive);

        assertThat(settings.isElementActive(), is(isElementActive));
    }


    @Test
    public void should_return_default_media_mode_if_is_not_provided() {
        Settings settings = new Settings("en", "en", null, false, false);

        assertThat(settings.getMediaListMode(), is(ISettingsRepository.MediaListMode.GRID));
    }
    
    @Test
    public void should_return_media_mode_if_is_provided() {
        Settings settings = new Settings("en", "en", ISettingsRepository.MediaListMode.LIST, false, false);

        assertThat(settings.getMediaListMode(), is(ISettingsRepository.MediaListMode.LIST));
    }
}
