package org.eyeseetea.malariacare.domain.entity.intent;

import java.util.HashMap;
import org.eyeseetea.malariacare.domain.entity.intent.Auth;

public class ConnectVoucher {
    private Auth auth;
    private HashMap<String, String> values;

    public ConnectVoucher() {
    }

    public ConnectVoucher(Auth auth,
            HashMap<String, String> values) {
        this.auth = auth;
        this.values = values;
    }

    public Auth getAuth() {
        return auth;
    }

    public HashMap<String, String> getValues() {
        return values;
    }
}
