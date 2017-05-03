package org.eyeseetea.malariacare.data.sync.exporter.model;

public class SurveyWSResponseOrgUnit {
    private OrgUnitWSResponse orgUnit;

    public SurveyWSResponseOrgUnit() {
    }

    public SurveyWSResponseOrgUnit(OrgUnitWSResponse orgUnit) {
        this.orgUnit = orgUnit;
    }

    public OrgUnitWSResponse getOrgUnit() {
        return orgUnit;
    }

    public void setOrgUnit(OrgUnitWSResponse orgUnit) {
        this.orgUnit = orgUnit;
    }
}
