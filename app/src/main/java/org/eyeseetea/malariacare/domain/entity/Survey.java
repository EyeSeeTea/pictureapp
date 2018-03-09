package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import java.util.Date;

public class Survey {
    private long id;
    private int status;
    private SurveyAnsweredRatio mSurveyAnsweredRatio;
    private Date mSurveyDate;
    private Program mProgram;
    private OrganisationUnit mOrganisationUnit;
    private UserAccount mUserAccount;
    private int mType;

    public Survey(Program program,
            OrganisationUnit organisationUnit,
            UserAccount userAccount, int type) {
        mProgram = required(program, "Program is required");
        mOrganisationUnit = organisationUnit;
        mUserAccount = userAccount;
        mType = required(type, "Type is required");
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public SurveyAnsweredRatio getSurveyAnsweredRatio() {
        return mSurveyAnsweredRatio;
    }

    public Program getProgram() {
        return mProgram;
    }

    public OrganisationUnit getOrganisationUnit() {
        return mOrganisationUnit;
    }

    public UserAccount getUserAccount() {
        return mUserAccount;
    }

    public int getType() {
        return mType;
    }

    @Override
    public String toString() {
        return "Survey{" +
                "id=" + id +
                ", status=" + status +
                '}';
    }

    public Date getSurveyDate() {
        return mSurveyDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Survey survey = (Survey) o;

        if (id != survey.id) return false;
        if (status != survey.status) return false;
        if (mSurveyAnsweredRatio != null ? !mSurveyAnsweredRatio.equals(survey.mSurveyAnsweredRatio)
                : survey.mSurveyAnsweredRatio != null) {
            return false;
        }
        return mSurveyDate != null ? mSurveyDate.equals(survey.mSurveyDate)
                : survey.mSurveyDate == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + status;
        result = 31 * result + (mSurveyAnsweredRatio != null ? mSurveyAnsweredRatio.hashCode() : 0);
        result = 31 * result + (mSurveyDate != null ? mSurveyDate.hashCode() : 0);
        return result;
    }
}
