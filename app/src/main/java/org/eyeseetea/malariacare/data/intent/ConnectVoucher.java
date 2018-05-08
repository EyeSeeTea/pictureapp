package org.eyeseetea.malariacare.data.intent;

import java.util.HashMap;

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
