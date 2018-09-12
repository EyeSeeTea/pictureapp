package org.eyeseetea.malariacare.data.sync.exporter.model;

public class SettingsSummary {
    private String dhisServerUrl;
    private String webServiceUrl;
    private String webUrl;
    private boolean downloadOver3g;
    private String fontSize;
    private boolean elements;
    private String language;

    public SettingsSummary(String dhisServerUrl, String webServiceUrl, String webUrl,
            boolean downloadOver3g, String fontSize, boolean elements, String language) {
        this.dhisServerUrl = dhisServerUrl;
        this.webServiceUrl = webServiceUrl;
        this.webUrl = webUrl;
        this.downloadOver3g = downloadOver3g;
        this.fontSize = fontSize;
        this.elements = elements;
        this.language = language;
    }

    public String getDhisServerUrl() {
        return dhisServerUrl;
    }

    public void setDhisServerUrl(String dhisServerUrl) {
        this.dhisServerUrl = dhisServerUrl;
    }

    public String getWebServiceUrl() {
        return webServiceUrl;
    }

    public void setWebServiceUrl(String webServiceUrl) {
        this.webServiceUrl = webServiceUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public boolean isDownloadOver3g() {
        return downloadOver3g;
    }

    public void setDownloadOver3g(boolean downloadOver3g) {
        this.downloadOver3g = downloadOver3g;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isElements() {
        return elements;
    }

    public void setElements(boolean elements) {
        this.elements = elements;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
