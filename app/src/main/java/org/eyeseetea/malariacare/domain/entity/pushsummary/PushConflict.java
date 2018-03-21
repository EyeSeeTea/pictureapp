package org.eyeseetea.malariacare.domain.entity.pushsummary;

public class PushConflict {
    private String uid;

    private String value;

    public PushConflict(String object, String value) {
        this.uid = object;
        this.value = value;
    }

    public String getUid() {
        return uid;
    }

    public String getValue() {
        return value;
    }

}
