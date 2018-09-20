package org.eyeseetea.malariacare.data.remote.model;

import java.util.List;

public class AuthResponse {

    private boolean authorized;

    private AuthCountry country;

    private String orgUnitId;

    private String orgUnitName;

    private List<AuthOrgUnitGroup> orgUnitGroups;

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    public AuthCountry getCountry() {
        return country;
    }

    public void setCountry(AuthCountry country) {
        this.country = country;
    }

    public String getOrgUnitId() {
        return orgUnitId;
    }

    public void setOrgUnitId(String orgUnitId) {
        this.orgUnitId = orgUnitId;
    }

    public String getOrgUnitName() {
        return orgUnitName;
    }

    public void setOrgUnitName(String orgUnitName) {
        this.orgUnitName = orgUnitName;
    }

    public List<AuthOrgUnitGroup> getOrgUnitGroups() {
        return orgUnitGroups;
    }

    public void setOrgUnitGroups(
            List<AuthOrgUnitGroup> orgUnitGroups) {
        this.orgUnitGroups = orgUnitGroups;
    }
}
