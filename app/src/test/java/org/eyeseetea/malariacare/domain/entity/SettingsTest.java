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

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                null, null, null, null);

        assertThat(settings.getLanguage(), is(systemLanguage));
    }

    @Test
    public void should_return_es_if_system_language_es_and_current_language_is_empty() {
        String systemLanguage = "es";
        String currentLanguage = "";

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                null, null, null, null);

        assertThat(settings.getLanguage(), is(systemLanguage));
    }

    @Test
    public void should_return_sw_if_current_language_was_changed_to_sw() {
        String systemLanguage = "es";
        String currentLanguage = "sw";

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false, false,
                null, null, null, null);

        assertThat(settings.getLanguage(), is(currentLanguage));
    }

    @Test
    public void should_throw_exception_if_system_language_is_null() {
        String systemLanguage = null;
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage, null, false, false, false, null, null, null,
                null);
    }

    @Test
    public void should_throw_exception_if_system_language_is_empty() {
        String systemLanguage = "";
        String currentLanguage = "sw";

        thrown.expect(IllegalArgumentException.class);

        new Settings(systemLanguage, currentLanguage, null, false, false, false, null, null, null,
                null);
    }

    @Test
    public void should_return_canDownloadWith3G_true_if_canDownloadWith3G_is_true() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean canDownloadWith3G = true;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, canDownloadWith3G,
                false, false, null, null, null, null);

        assertThat(settings.canDownloadWith3G(), is(canDownloadWith3G));
    }

    @Test
    public void should_return_canDownloadWith3G_false_if_canDownloadWith3G_is_false() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean canDownloadWith3G = false;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, canDownloadWith3G,
                false, false, null, null, null, null);

        assertThat(settings.canDownloadWith3G(), is(canDownloadWith3G));
    }

    @Test
    public void should_return_isElementActive_true_if_isElementActive_is_true() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean isElementActive = true;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false,
                isElementActive, false, null, null, null, null);

        assertThat(settings.isElementActive(), is(isElementActive));
    }

    @Test
    public void should_return_isElementActive_false_if_isElementActive_is_false() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean isElementActive = false;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false,
                isElementActive, false, null, null, null, null);

        assertThat(settings.isElementActive(), is(isElementActive));
    }


    @Test
    public void should_return_isMetadataUpdateActive_false_if_isMetadataUpdateActive_is_false() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean isMetadataUpdateActive = false;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false,
                isMetadataUpdateActive, null, null, null, null);

        assertThat(settings.isMetadataUpdateActive(), is(isMetadataUpdateActive));
    }


    @Test
    public void should_return_isMetadataUpdateActive_true_if_isMetadataUpdateActive_is_true() {
        String systemLanguage = "es";
        String currentLanguage = null;
        boolean isMetadataUpdateActive = true;

        Settings settings = new Settings(systemLanguage, currentLanguage, null, false, false,
                isMetadataUpdateActive, null, null, null, null);

        assertThat(settings.isMetadataUpdateActive(), is(isMetadataUpdateActive));
    }

    @Test
    public void should_return_default_media_mode_if_is_not_provided() {
        Settings settings = new Settings("en", "en", null, false, false, false, null, null, null,
                null);

        assertThat(settings.getMediaListMode(), is(ISettingsRepository.MediaListMode.GRID));
    }
    
    @Test
    public void should_return_media_mode_if_is_provided() {
        Settings settings = new Settings("en", "en", ISettingsRepository.MediaListMode.LIST, false,
                false, false, null, null, null, null);

        assertThat(settings.getMediaListMode(), is(ISettingsRepository.MediaListMode.LIST));
    }

    @Test
    public void should_return_dhisServerUrl_if_provided() {
        String dhisServerURl = "dhisServerUrl";
        Settings settings = new Settings("en", "en", null, false, false, false, dhisServerURl,
                null, null, null);
        assertThat(settings.getDhisServerUrl(), is(dhisServerURl));
    }

    @Test
    public void should_return_webServiceUrl_if_provided() {
        String webServiceUrl = "webServiceUrl";
        Settings settings = new Settings("en", "en", null, false, false, false, null,
                webServiceUrl, null, null);
        assertThat(settings.getWsServerUrl(), is(webServiceUrl));
    }

    @Test
    public void should_return_webUrl_if_provided() {
        String webUrl = "webUrl";
        Settings settings = new Settings("en", "en", null, false, false, false, null,
                null, webUrl, null);
        assertThat(settings.getWebUrl(), is(webUrl));
    }

    @Test
    public void should_return_font_size() {
        String fontSize = "fontSize";
        Settings settings = new Settings("en", "en", null, false, false, false, null,
                null, null, fontSize);
        assertThat(settings.getFontSize(), is(fontSize));
    }

}
