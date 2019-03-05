package org.eyeseetea.malariacare.presentation.models;

import java.util.Date;
import java.util.List;

public class SurveyViewModel {

    private final Date eventDate;
    private final String uid;
    private final String voucherUid;
    private final boolean isCompleted;
    private final boolean hasPhone;
    private final boolean noIssueVoucher;
    private final List<String> importantValues;
    private final List<String> visibleValues;
    private final int status;

    public SurveyViewModel(Date eventDate, String uid, String voucherUid,
            boolean isCompleted, boolean hasPhone, boolean noIssueVoucher,
            List<String> importantValues, List<String> visibleValues, int status) {
        this.eventDate = eventDate;
        this.uid = uid;
        this.voucherUid = voucherUid;
        this.isCompleted = isCompleted;
        this.hasPhone = hasPhone;
        this.noIssueVoucher = noIssueVoucher;
        this.importantValues = importantValues;
        this.visibleValues = visibleValues;
        this.status = status;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public String getUid() {
        return uid;
    }

    public String getVoucherUid() {
        return voucherUid;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public List<String> getImportantValues() {
        return importantValues;
    }

    public List<String> getVisibleValues() {
        return visibleValues;
    }

    public boolean hasPhone() {
        return hasPhone;
    }

    public boolean noIssueVoucher() {
        return noIssueVoucher;
    }

    public int getStatus() {
        return status;
    }
}
