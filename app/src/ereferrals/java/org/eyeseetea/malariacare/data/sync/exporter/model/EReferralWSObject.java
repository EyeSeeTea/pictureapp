package org.eyeseetea.malariacare.data.sync.exporter.model;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

import java.util.ArrayList;
import java.util.List;

public class EReferralWSObject {
    private String version;
    private String source;
    private String userName;
    private String password;
    private List<Action> actions;

    public EReferralWSObject() {
        version = PreferencesState.getInstance().getContext().getString(R.string.ws_version);
        actions = new ArrayList<>();
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}

