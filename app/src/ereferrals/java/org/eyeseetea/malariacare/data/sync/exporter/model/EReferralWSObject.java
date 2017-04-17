package org.eyeseetea.malariacare.data.sync.exporter.model;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

import java.util.ArrayList;
import java.util.List;

public class EReferralWSObject {
    private String version;
    private List<Object> referrals;

    public EReferralWSObject() {
        version = PreferencesState.getInstance().getContext().getString(R.string.ws_version);
        referrals=new ArrayList<>();
    }


    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Object> getReferrals() {
        return referrals;
    }

    public void setReferrals(List<Object> referrals) {
        this.referrals = referrals;
    }
}
