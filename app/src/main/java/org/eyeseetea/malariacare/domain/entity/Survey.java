package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import org.eyeseetea.malariacare.domain.UidsAndCodes;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.Date;
import java.util.List;

public class Survey {
    private long id;
    private String uid;
    private int status;
    private SurveyAnsweredRatio mSurveyAnsweredRatio;
    private Date mSurveyDate;
    private String programUid;
    private String orgUnitUid;
    private String userUid;
    private int mType;
    private List<Value> values;
    private String voucherUid;
    private String visibleVoucherUid;

    public Survey(long id, String uid, String voucherUid, int status,
            SurveyAnsweredRatio surveyAnsweredRatio, Date surveyDate,
            String programUid, String orgUnitUid, String userUid, int type,
            List<Value> values, String visibleVoucherUid) {
        this.id = id;
        this.uid = uid;
        this.voucherUid = voucherUid;
        this.status = status;
        this.mSurveyAnsweredRatio = surveyAnsweredRatio;
        this.mSurveyDate = surveyDate;
        this.programUid = required(programUid, "programUid is required");
        this.orgUnitUid = orgUnitUid;
        this.userUid = userUid;
        this.mType = required(type, "Type is required");
        this.values = values;
        this.visibleVoucherUid = visibleVoucherUid;
    }

    public Survey(String programUid, String orgUnitUid, String userUid, int type) {
        this.programUid = required(programUid, "programUid is required");
        this.orgUnitUid = orgUnitUid;
        this.userUid = userUid;
        this.mType = required(type, "Type is required");
    }

    public static Survey createNewSurvey(String programUid, String userUid) {
        Survey survey = new Survey(programUid, null, userUid, Constants.SURVEY_NO_TYPE);
        survey.setStatus(Constants.SURVEY_IN_PROGRESS);
        return survey;
    }

    public Survey(Date surveyDate) {
        mSurveyDate = surveyDate;
    }

    public Survey(long id) {
        this.id = id;
    }

    public Survey(long id, int status,
            SurveyAnsweredRatio surveyAnsweredRatio) {
        this.id = id;
        this.status = status;
        mSurveyAnsweredRatio = surveyAnsweredRatio;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getUid() {
        return uid;
    }

    public void assignUid(String uid) {
        this.uid = uid;
    }

    public String getProgramUid() {
        return programUid;
    }

    public String getOrgUnitUid() {
        return orgUnitUid;
    }

    public String getUserUid() {
        return userUid;
    }

    public List<Value> getValues() {
        return values;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public SurveyAnsweredRatio getSurveyAnsweredRatio() {
        return mSurveyAnsweredRatio;
    }


    public int getType() {
        return mType;
    }


    public Date getSurveyDate() {
        return mSurveyDate;
    }

    public void changeEventDate(Date date) {
        mSurveyDate = date;
    }

    public String getUId() {
        return uid;
    }

    public String getVoucherUid() {
        return voucherUid;
    }

    public void assignVoucherUid(String voucherUid) {
        this.voucherUid = voucherUid;
    }

    public String getVisibleVoucherUid() {
        return visibleVoucherUid;
    }
    public void assignVisibleVoucherUid(String visibleVoucherUid) {
        this.visibleVoucherUid = visibleVoucherUid;
    }

    public boolean isCompleted() {
        return this.status == Constants.SURVEY_COMPLETED;
    }

    public boolean hasPhone() {
        boolean hasPhone = false;

        for (Value value : values) {
            if (value.questionUId.equals(UidsAndCodes.PHONE_QUESTION_UID)) {
                hasPhone = true;
            }
        }

        return hasPhone;
    }

    public boolean noIssueVoucher() {
        boolean noIssueVoucher = false;

        for (Value value : values) {
            if (value.optionCode != null &&
                    value.questionUId.equals(UidsAndCodes.NO_VOUCHER_QUESTION_UID) &&
                    value.optionCode.equals(UidsAndCodes.NO_VOUCHER_OPTION_CODE)) {
                noIssueVoucher = true;
            }
        }

        return noIssueVoucher;
    }

    @Override
    public String toString() {
        return "Survey{" +
                "id=" + id +
                ", uid=" + uid +
                ", status=" + status +
                ", mSurveyAnsweredRatio=" + mSurveyAnsweredRatio +
                ", mSurveyDate=" + mSurveyDate +
                ", programUid=" + programUid +
                ", orgUnitUid=" + orgUnitUid +
                ", userUid=" + userUid +
                ", mType=" + mType +
                ", values =" + values +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Survey survey = (Survey) o;

        if (id != survey.id) return false;
        if (status != survey.status) return false;
        if (mType != survey.mType) return false;
        if (mSurveyAnsweredRatio != null ? !mSurveyAnsweredRatio.equals(survey.mSurveyAnsweredRatio)
                : survey.mSurveyAnsweredRatio != null) {
            return false;
        }
        if (mSurveyDate != null ? !mSurveyDate.equals(survey.mSurveyDate)
                : survey.mSurveyDate != null) {
            return false;
        }
        if (uid != null ? !uid.equals(survey.uid)
                : survey.uid != null) {
            return false;
        }
        if (orgUnitUid != null ? !orgUnitUid.equals(survey.orgUnitUid)
                : survey.orgUnitUid != null) {
            return false;
        }
        if (orgUnitUid != null ? !orgUnitUid.equals(survey.orgUnitUid)
                : survey.orgUnitUid != null) {
            return false;
        }
        if (userUid != null ? !userUid.equals(survey.userUid)
                : survey.userUid != null) {
            return false;
        }

        return values != null ? values.equals(survey.values) : survey.values == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + status;
        result = 31 * result + (mSurveyAnsweredRatio != null ? mSurveyAnsweredRatio.hashCode() : 0);
        result = 31 * result + (mSurveyDate != null ? mSurveyDate.hashCode() : 0);
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        result = 31 * result + (programUid != null ? programUid.hashCode() : 0);
        result = 31 * result + (orgUnitUid != null ? orgUnitUid.hashCode() : 0);
        result = 31 * result + (userUid != null ? userUid.hashCode() : 0);
        result = 31 * result + mType;
        result = 31 * result + (values != null ? values.hashCode() : 0);
        return result;
    }
}
