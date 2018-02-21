package org.eyeseetea.malariacare.domain.entity;


import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class OrganisationUnitGroup {

    private String uid;
    private String code;

    public OrganisationUnitGroup(String uid, String code) {
        this.uid = required(uid,"UID is required");
        this.code = required(code,"Code is required");
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
