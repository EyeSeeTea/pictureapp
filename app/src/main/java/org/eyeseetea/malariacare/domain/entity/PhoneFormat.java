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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhoneFormat that = (PhoneFormat) o;

        if (phoneMask != null ? !phoneMask.equals(that.phoneMask) : that.phoneMask != null) {
            return false;
        }
        if (trunkPrefix != null ? !trunkPrefix.equals(that.trunkPrefix)
                : that.trunkPrefix != null) {
            return false;
        }
        return prefixtToPut != null ? prefixtToPut.equals(that.prefixtToPut)
                : that.prefixtToPut == null;
    }

    @Override
    public int hashCode() {
        int result = phoneMask != null ? phoneMask.hashCode() : 0;
        result = 31 * result + (trunkPrefix != null ? trunkPrefix.hashCode() : 0);
        result = 31 * result + (prefixtToPut != null ? prefixtToPut.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PhoneFormat{" +
                "phoneMask='" + phoneMask + '\'' +
                ", trunkPrefix='" + trunkPrefix + '\'' +
                ", prefixtToPut='" + prefixtToPut + '\'' +
                '}';
    }
}
