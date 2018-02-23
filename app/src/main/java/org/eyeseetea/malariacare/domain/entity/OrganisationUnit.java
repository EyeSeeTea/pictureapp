package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OrganisationUnit {
    public static final String CLOSE_DATE_DESCRIPTION =
            "[%s] - Android Surveillance App set the closing date to %s because over 30 surveys "
                    + "were pushed within 1 hour.";
    public static final String CLOSE_DATE_FORMAT = "dd-MM-yyyy";
    private String uid;
    private String name;
    private String code;
    private String description;
    private Date closedDate;
    private boolean banned;
    private String pin;
    private Program mProgram;

    public OrganisationUnit(String uid, String name, String description, Date closedDate) {
        this.uid = required(uid,"UID is required");
        this.name = required(name,"Name is required");
        this.description = required(description,"Description is required");
        this.closedDate = closedDate;
    }

    public OrganisationUnit(String uid, String name, boolean banned) {
        this.uid = required(uid,"UID is required");
        this.name = required(name,"Name is required");
        this.banned = banned;
    }

    public OrganisationUnit(String uid, String name, String code, String description,
            Date closedDate) {
        this.uid = required(uid, "UID is required");
        this.name = required(name, "Name is required");
        this.code = required(code, "Code is required");
        this.description = description;
        this.closedDate = closedDate;
    }

    public OrganisationUnit(String uid, String name, String code, String description,
            Date closedDate, String pin, Program program) {
        this.uid = required(uid,"UID is required");
        this.name = required(name,"Name is required");
        this.code = required(code,"Code is required");
        this.pin = required(pin,"Pin is required");
        this.description = description;
        this.closedDate = closedDate;
        mProgram = required(program,"Program is required");
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

        if (description != null) {
            sb.append(description);
            sb.append("");//next line
            sb.append("");//next line
        }

        sb.append(descriptionAdded);
        return sb.toString();
    }

    public String getPin() {
        return pin;
    }

    public Program getProgram() {
        return mProgram;
    }

    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrganisationUnit that = (OrganisationUnit) o;

        if (banned != that.banned) return false;
        if (uid != null ? !uid.equals(that.uid) : that.uid != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        if (description != null ? !description.equals(that.description)
                : that.description != null) {
            return false;
        }
        if (closedDate != null ? !closedDate.equals(that.closedDate) : that.closedDate != null) {
            return false;
        }
        if (pin != null ? !pin.equals(that.pin) : that.pin != null) return false;
        return mProgram != null ? mProgram.equals(that.mProgram) : that.mProgram == null;
    }

    @Override
    public int hashCode() {
        int result = uid != null ? uid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (closedDate != null ? closedDate.hashCode() : 0);
        result = 31 * result + (banned ? 1 : 0);
        result = 31 * result + (pin != null ? pin.hashCode() : 0);
        result = 31 * result + (mProgram != null ? mProgram.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OrganisationUnit{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", closedDate=" + closedDate +
                ", banned=" + banned +
                ", pin='" + pin + '\'' +
                ", mProgram=" + mProgram +
                '}';
    }
}
