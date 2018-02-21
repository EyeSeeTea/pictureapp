package org.eyeseetea.malariacare.domain.entity;


import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class OrganisationUnitGroup {

    private String uid;
    private String name;

    public OrganisationUnitGroup(String uid, String name) {
        this.uid = required(uid,"UID is required");
        this.name = required(name,"Name is required");
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
