package org.eyeseetea.malariacare.data.sync.exporter.model;

public class SurveyWSResponseOrgUnit {
    private OrgUnitWSResponse orgUnit;
    private String msg;
    private int returnCode;

    public SurveyWSResponseOrgUnit() {
    }

    public SurveyWSResponseOrgUnit(
            OrgUnitWSResponse orgUnit, String msg, int returnCode) {
        this.orgUnit = orgUnit;
        this.msg = msg;
        this.returnCode = returnCode;
    }

    public OrgUnitWSResponse getOrgUnit() {
        return orgUnit;
    }

    public void setOrgUnit(OrgUnitWSResponse orgUnit) {
        this.orgUnit = orgUnit;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }
}
