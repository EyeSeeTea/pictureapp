package org.eyeseetea.malariacare.domain.entity;

import java.util.Date;

public class OrganisationUnit {
    private String uid;
    private String name;
    private String description;
    private Date closedDate;

    public OrganisationUnit(String uid, String name, String description, Date closedDate) {
        this.uid = uid;
        this.name = name;
        this.description = description;
        this.closedDate = closedDate;
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
}
