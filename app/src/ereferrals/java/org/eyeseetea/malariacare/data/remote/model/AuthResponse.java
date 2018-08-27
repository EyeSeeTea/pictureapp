package org.eyeseetea.malariacare.data.remote.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponse {

    private String authorized;

    @JsonProperty("code of OU level: country")
    private String country;

    private String orgUnitId;

    private String orgUnitName;

    private String orgUnitGroups;


    public String getAuthorized() {
        return authorized;
    }

    public void setAuthorized(String authorized) {
        this.authorized = authorized;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
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

    public String getOrgUnitGroups() {
        return orgUnitGroups;
    }

    public void setOrgUnitGroups(String orgUnitGroups) {
        this.orgUnitGroups = orgUnitGroups;
    }

    @Override
    public String toString() {
        return "ClassPojo [authorized = " + authorized + ", orgUnitId = " + orgUnitId
                + ", orgUnitName = " + orgUnitName + ", orgUnitGroups = " + orgUnitGroups + "]";
    }

}
