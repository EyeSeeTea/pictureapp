package org.eyeseetea.malariacare.domain.entity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OrganisationUnit {
    private static final String CLOSE_DATE_DESCRIPTION =
            "[%s] - Android Surveillance App set the closing date to %s because over 30 surveys "
                    + "were pushed within 1 hour.";
    private static final String CLOSE_DATE_FORMAT = "";
    private String uid;
    private String name;
    private String description;
    private Date closedDate;
    private boolean banned;

    public OrganisationUnit(String uid, String name, String description, Date closedDate) {
        this.uid = uid;
        this.name = name;
        this.description = description;
        this.closedDate = closedDate;
    }

    public OrganisationUnit(String uid, String name, boolean banned) {
        this.uid = uid;
        this.name = name;
        this.banned = banned;
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
        return banned || (closedDate != null && closedDate.before(new Date()));
    }

    public void ban() {
        closedDate = getNewClosingDate();
        String closeDateText = getStringFromDateWithFormat(closedDate, CLOSE_DATE_FORMAT);
        String bannedDescription = String.format(CLOSE_DATE_DESCRIPTION, closedDate.getTime(),
                closeDateText);
        description = addDescription(bannedDescription);
    }

    private Date getNewClosingDate() {
        Calendar sysDate = Calendar.getInstance();
        sysDate.setTime(new Date());
        sysDate.set(Calendar.HOUR, sysDate.get(Calendar.HOUR) - 24);
        return sysDate.getTime();
    }

    private String getStringFromDateWithFormat(Date date, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.US);
        return format.format(date.getTime());
    }

    private String addDescription(String descriptionAdded) {
        StringBuilder sb = new StringBuilder();
        sb.append(description);
        sb.append("");//next line
        sb.append("");//next line
        sb.append(descriptionAdded);
        return sb.toString();
    }
}
