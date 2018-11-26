package org.eyeseetea.malariacare.domain.entity.intent;

import java.util.HashMap;
import org.eyeseetea.malariacare.domain.entity.intent.Auth;

public class ConnectVoucher {
    private Auth auth;
    private HashMap<String, String> values;
    private String uId;

    public ConnectVoucher() {
    }

    public ConnectVoucher(Auth auth,
            HashMap<String, String> values, String uId) {
        this.auth = auth;
        this.values = values;
        this.uId = uId;
    }

    public Auth getAuth() {
        return auth;
    }

    public HashMap<String, String> getValues() {
        return values;
    }

    public String getUid() {
        return uId;
    }
}
