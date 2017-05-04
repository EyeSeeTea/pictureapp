package org.eyeseetea.malariacare.domain.entity;


public class OrgUnit {
    private String code;
    private String id;
    private String pin;
    private Program mProgram;


    public OrgUnit() {
    }

    public OrgUnit(String code, String id, String pin,
            Program program) {
        this.code = code;
        this.id = id;
        this.pin = pin;
        mProgram = program;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Program getProgram() {
        return mProgram;
    }

    public void setProgram(Program program) {
        mProgram = program;
    }
}
