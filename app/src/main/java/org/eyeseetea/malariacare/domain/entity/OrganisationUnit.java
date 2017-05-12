package org.eyeseetea.malariacare.domain.entity;

import java.util.Date;

public class OrganisationUnit {
    private String uid;
    private String name;
    private String code;
    private String description;
    private Date closedDate;
    private String pin;
    private Program mProgram;

    public OrganisationUnit(String uid, String name, String description, Date closedDate) {
        this.uid = uid;
        this.name = name;
        this.description = description;
        this.closedDate = closedDate;
    }

    public OrganisationUnit(String uid, String name, String code, String description,
            Date closedDate, String pin, Program program) {
        this.uid = uid;
        this.name = name;
        this.code = code;
        this.description = description;
        this.closedDate = closedDate;
        this.pin = pin;
        mProgram = program;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public boolean isBanned() {
        return closedDate != null && closedDate.before(new Date());
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
