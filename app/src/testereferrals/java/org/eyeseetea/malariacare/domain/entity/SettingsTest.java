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
    String programUrl = "https://url.es";
    String programUser = "user";
    String programPass = "password";

    @Test
    public void should_return_user_program() {
        String systemLanguage = "es";
        String currentLanguage = null;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false, programUrl, programUser, programPass);

        assertThat(settings.getUser(), is(programUser));
    }

    @Test
    public void should_return_password_program() {
        String systemLanguage = "es";
        String currentLanguage = null;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false, programUrl, programUser, programPass);

        assertThat(settings.getPass(), is(programPass));
    }

    @Test
    public void should_return_url_program() {
        String systemLanguage = "es";
        String currentLanguage = null;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false, programUrl, programUser, programPass);

        assertThat(settings.getUrl(), is(programUrl));
    }

    @Test
    public void should_return_es_if_system_language_es_and_current_language_is_null() {
        String systemLanguage = "es";
        String currentLanguage = null;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false, programUrl, programUser, programPass);

        assertThat(settings.getLanguage(), is(systemLanguage));
    }

    @Test
    public void should_return_es_if_system_language_es_and_current_language_is_empty() {
        String systemLanguage = "es";
        String currentLanguage = "";

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false, programUrl, programUser, programPass);

        assertThat(settings.getLanguage(), is(systemLanguage));
    }

    @Test
    public void should_return_sw_if_current_language_was_changed_to_sw() {
        String systemLanguage = "es";
        String currentLanguage = "sw";

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false, programUrl, programUser, programPass);

        assertThat(settings.getLanguage(), is(currentLanguage));
    }

    @Test
    public void should_throw_exception_if_system_language_is_null() {
        String systemLanguage = null;
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage, null, false, false, false, programUrl, programUser, programPass);
    }

    @Test
    public void should_throw_exception_if_system_language_is_empty() {
        String systemLanguage = "";
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage, null, false, false, false, programUrl, programUser, programPass);
    }

    @Test
    public void should_throw_exception_if_user_is_empty() {
        String systemLanguage = "";
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage, null, false, false, false, programUrl, null, programPass);
    }

    @Test
    public void should_throw_exception_if_pass_is_empty() {
        String systemLanguage = "";
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage, null, false, false, false, programUrl, programUser, null);
    }

    @Test
    public void should_throw_exception_if_url_is_empty() {
        String systemLanguage = "";
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage, null, false, false, false, null, programUser, programPass);
    }

    @Test
    public void should_return_canDownloadWith3G_true_if_canDownloadWith3G_is_true() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean canDownloadWith3G = true;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, canDownloadWith3G, false, false, programUrl, programUser, programPass);

        assertThat(settings.canDownloadWith3G(), is(canDownloadWith3G));
    }

    @Test
    public void should_return_canDownloadWith3G_false_if_canDownloadWith3G_is_false() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean canDownloadWith3G = false;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, canDownloadWith3G, false, false, programUrl, programUser, programPass);

        assertThat(settings.canDownloadWith3G(), is(canDownloadWith3G));
    }

    @Test
    public void should_return_isElementActive_true_if_isElementActive_is_true() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean isElementActive = true;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, isElementActive, false, programUrl, programUser, programPass);

        assertThat(settings.isElementActive(), is(isElementActive));
    }

    @Test
    public void should_return_isElementActive_false_if_isElementActive_is_false() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean isElementActive = false;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, isElementActive, false, programUrl, programUser, programPass);

        assertThat(settings.isElementActive(), is(isElementActive));
    }


    @Test
    public void should_return_isMetadataUpdateActive_false_if_isMetadataUpdateActive_is_false() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean isMetadataUpdateActive = false;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, isMetadataUpdateActive, programUrl, programUser, programPass);

        assertThat(settings.isMetadataUpdateActive(), is(isMetadataUpdateActive));
    }


    @Test
    public void should_return_isMetadataUpdateActive_true_if_isMetadataUpdateActive_is_true() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean isMetadataUpdateActive = true;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, isMetadataUpdateActive, programUrl, programUser, programPass);

        assertThat(settings.isMetadataUpdateActive(), is(isMetadataUpdateActive));
    }

    @Test
    public void should_return_default_media_mode_if_is_not_provided() {
        Settings settings = new Settings("en", "en", null, false, false, false, programUrl, programUser, programPass);

        assertThat(settings.getMediaListMode(), is(ISettingsRepository.MediaListMode.GRID));
    }
    
    @Test
    public void should_return_media_mode_if_is_provided() {
        Settings settings = new Settings("en", "en", ISettingsRepository.MediaListMode.LIST, false, false, false, programUrl, programUser, programPass);

        assertThat(settings.getMediaListMode(), is(ISettingsRepository.MediaListMode.LIST));
    }
}
