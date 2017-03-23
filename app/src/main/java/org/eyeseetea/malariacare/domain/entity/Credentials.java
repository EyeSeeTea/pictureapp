package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import org.hisp.dhis.client.sdk.android.api.D2;

public class Credentials {
    private static final String DEMO_USER = "demo";
    private static final String DEMO_SERVER = "demo.server";

    private String username;
    private String password;
    private String serverURL;

    public Credentials(String serverURL, String username, String password) {
        this.serverURL = required(serverURL, "Server URL is required");
        this.username = required(username, "Username is required");
        this.password = required(password, "Password is required");
    }

    public static Credentials createDemoCredentials() {
        Credentials credentials = new Credentials(DEMO_SERVER, DEMO_USER, DEMO_USER);

        return credentials;
    }

    public String getServerURL() {
        return serverURL;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isDemoCredentials() {
        return this.equals(Credentials.createDemoCredentials());
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Credentials)) return false;

        Credentials other = (Credentials) o;
        if (!this.serverURL.equals(other.getServerURL())) return false;
        if (!this.username.equals(other.getUsername())) return false;
        if (!this.password.equals(other.getPassword())) return false;

        return true;
    }

    public int hashCode() {
        return (int) serverURL.hashCode() *
                username.hashCode() *
                password.hashCode();
    }

    public boolean isLogged() {
        //// FIXME: 21/03/2017 It should be review on the lao/cambodia user login
        try {
            return D2.me().isSignedIn().toBlocking().first();
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }
}
