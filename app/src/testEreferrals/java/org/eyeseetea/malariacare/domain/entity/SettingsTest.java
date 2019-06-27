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

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);

        assertThat(settings.getUser(), is(programUser));
    }

    @Test
    public void should_return_password_program() {
        String systemLanguage = "es";
        String currentLanguage = null;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);

        assertThat(settings.getPass(), is(programPass));
    }

    @Test
    public void should_return_url_program() {
        String systemLanguage = "es";
        String currentLanguage = null;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);

        assertThat(settings.getWsServerUrl(), is(programUrl));
    }

    @Test
    public void should_return_es_if_system_language_es_and_current_language_is_null() {
        String systemLanguage = "es";
        String currentLanguage = null;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);

        assertThat(settings.getLanguage(), is(systemLanguage));
    }

    @Test
    public void should_return_es_if_system_language_es_and_current_language_is_empty() {
        String systemLanguage = "es";
        String currentLanguage = "";

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);

        assertThat(settings.getLanguage(), is(systemLanguage));
    }

    @Test
    public void should_return_sw_if_current_language_was_changed_to_sw() {
        String systemLanguage = "es";
        String currentLanguage = "sw";

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);

        assertThat(settings.getLanguage(), is(currentLanguage));
    }

    @Test
    public void should_throw_exception_if_system_language_is_null() {
        String systemLanguage = null;
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);
    }

    @Test
    public void should_throw_exception_if_user_is_empty() {
        String systemLanguage = "";
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);
    }

    @Test
    public void should_throw_exception_if_pass_is_empty() {
        String systemLanguage = "";
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);
    }

    @Test
    public void should_throw_exception_if_url_is_empty() {
        String systemLanguage = "";
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);
    }

    @Test
    public void should_throw_exception_if_system_language_is_empty() {
        String systemLanguage = "";
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);
    }

    @Test
    public void should_return_canDownloadWith3G_true_if_canDownloadWith3G_is_true() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean canDownloadWith3G = true;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, true, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);

        assertThat(settings.canDownloadWith3G(), is(canDownloadWith3G));
    }

    @Test
    public void should_return_canDownloadWith3G_false_if_canDownloadWith3G_is_false() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean canDownloadWith3G = false;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);

        assertThat(settings.canDownloadWith3G(), is(canDownloadWith3G));
    }

    @Test
    public void should_return_isElementActive_true_if_isElementActive_is_true() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean isElementActive = true;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, true, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);

        assertThat(settings.isElementActive(), is(isElementActive));
    }

    @Test
    public void should_return_isElementActive_false_if_isElementActive_is_false() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean isElementActive = false;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);

        assertThat(settings.isElementActive(), is(isElementActive));
    }


    @Test
    public void should_return_isMetadataUpdateActive_false_if_isMetadataUpdateActive_is_false() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean isMetadataUpdateActive = false;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);

        assertThat(settings.isMetadataUpdateActive(), is(isMetadataUpdateActive));
    }


    @Test
    public void should_return_isMetadataUpdateActive_true_if_isMetadataUpdateActive_is_true() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean isMetadataUpdateActive = true;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, true,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);

        assertThat(settings.isMetadataUpdateActive(), is(isMetadataUpdateActive));
    }

    @Test
    public void should_return_default_media_mode_if_is_not_provided() {
        String systemLanguage = "en";
        String currentLanguage = "en";
        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);

        assertThat(settings.getMediaListMode(), is(ISettingsRepository.MediaListMode.GRID));
    }

    @Test
    public void should_return_media_mode_if_is_provided() {
        String systemLanguage = "en";
        String currentLanguage = "en";
        Settings settings = new Settings(systemLanguage, currentLanguage,
                ISettingsRepository.MediaListMode.LIST, false, false, false,
                programUser, programPass, programUrl, null, null, null, null, false, false, "1",true);

        assertThat(settings.getMediaListMode(), is(ISettingsRepository.MediaListMode.LIST));
    }

    @Test
    public void should_return_webServiceUrl_if_provided() {
        String systemLanguage = "en";
        String currentLanguage = "en";
        String webServiceUrl = "webServiceUrl";
        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, webServiceUrl, null, null, null, null, false, false, "1",true);
        assertThat(settings.getWsServerUrl(), is(webServiceUrl));
    }

    @Test
    public void should_return_webUrl_if_provided() {
        String systemLanguage = "en";
        String currentLanguage = "en";
        String webUrl = "webUrl";
        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, webUrl, null, null, null, false, false, "1",true);
        assertThat(settings.getWebUrl(), is(webUrl));
    }

    @Test
    public void should_return_font_size() {
        String systemLanguage = "en";
        String currentLanguage = "en";
        String fontSize = "fontSize";
        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, fontSize, null, null, false, false, "1",true);
        assertThat(settings.getFontSize(), is(fontSize));
    }

    @Test
    public void should_return_program_url() {
        String systemLanguage = "en";
        String currentLanguage = "en";
        String programUrl = "programUrl";
        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, programUrl, null, false, false, "1",true);
        assertThat(settings.getProgramUrl(), is(programUrl));
    }
    @Test
    public void should_return_program_endpoint() {
        String systemLanguage = "en";
        String currentLanguage = "en";
        String programEndpoint = "programEndpoint";
        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                programUser, programPass, programUrl, null, null, null, programEndpoint, false,
                false, "1",true);
        assertThat(settings.getProgramEndPoint(), is(programEndpoint));
    }

}
