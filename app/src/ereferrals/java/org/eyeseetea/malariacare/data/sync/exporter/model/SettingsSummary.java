package org.eyeseetea.malariacare.data.sync.exporter.model;

public class SettingsSummary {
    private String dhisServerUrl;
    private String webSrviceUrl;
    private String webUrl;
    private String downloadOver3g;
    private String fontSize;
    private String elements;

    public SettingsSummary(String dhisServerUrl, String webSrviceUrl, String webUrl,
            String downloadOver3g, String fontSize, String elements) {
        this.dhisServerUrl = dhisServerUrl;
        this.webSrviceUrl = webSrviceUrl;
        this.webUrl = webUrl;
        this.downloadOver3g = downloadOver3g;
        this.fontSize = fontSize;
        this.elements = elements;
    }

    public String getDhisServerUrl() {
        return dhisServerUrl;
    }

    public void setDhisServerUrl(String dhisServerUrl) {
        this.dhisServerUrl = dhisServerUrl;
    }

    public String getWebSrviceUrl() {
        return webSrviceUrl;
    }

    public void setWebSrviceUrl(String webSrviceUrl) {
        this.webSrviceUrl = webSrviceUrl;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getDownloadOver3g() {
        return downloadOver3g;
    }

    public void setDownloadOver3g(String downloadOver3g) {
        this.downloadOver3g = downloadOver3g;
    }

    public String getFontSize() {
        return fontSize;
    }

    public void setFontSize(String fontSize) {
        this.fontSize = fontSize;
    }

    public String getElements() {
        return elements;
    }

    public void setElements(String elements) {
        this.elements = elements;
    }
}
