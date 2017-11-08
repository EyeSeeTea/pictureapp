package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class PhoneFormat {
    private String phoneMask;
    private String trunkPrefix;
    private String prefixtToPut;

    public PhoneFormat(String phoneMask, String trunkPrefix, String prefixtToPut) {
        this.phoneMask = required(phoneMask, "phoneMask is required");
        this.trunkPrefix = trunkPrefix;
        this.prefixtToPut = prefixtToPut;
    }

    public String getPhoneMask() {
        return phoneMask;
    }

    public String getTrunkPrefix() {
        return trunkPrefix;
    }

    public String getPrefixtToPut() {
        return prefixtToPut;
    }
}
