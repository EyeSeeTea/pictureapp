package org.eyeseetea.malariacare.network;

/**
 * Minimum VO to save the latest server url + version in a pojo
 * Created by arrizabalaga on 21/01/16.
 */
public class ServerInfo {
    private String url;
    private String version;

    public ServerInfo(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerInfo that = (ServerInfo) o;

        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        return !(version != null ? !version.equals(that.version) : that.version != null);

    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "url='" + url + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
