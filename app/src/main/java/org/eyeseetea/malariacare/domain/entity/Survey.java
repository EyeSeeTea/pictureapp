package org.eyeseetea.malariacare.domain.entity;

import org.eyeseetea.malariacare.utils.Constants;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import java.util.Date;
import java.util.List;

public class Survey {
    private long id;
    private int status;
    private SurveyAnsweredRatio mSurveyAnsweredRatio;
    private Date mSurveyDate;
    private Program mProgram;
    private OrganisationUnit mOrganisationUnit;
    private UserAccount mUserAccount;
    private int mType;
    private List<Question> questions;

    public Survey(long id, int status,
            SurveyAnsweredRatio surveyAnsweredRatio, Date surveyDate,
            Program program, OrganisationUnit organisationUnit,
            UserAccount userAccount, int type,
            List<Question> questions) {
        this.id = id;
        this.status = status;
        mSurveyAnsweredRatio = surveyAnsweredRatio;
        mSurveyDate = surveyDate;
        mProgram = required(program, "Program is required");
        mOrganisationUnit = organisationUnit;
        mUserAccount = userAccount;
        mType = required(type, "Type is required");
        this.questions = questions;
    }

    public Survey(Program program,
            OrganisationUnit organisationUnit,
            UserAccount userAccount, int type) {
        mProgram = required(program, "Program is required");
        mOrganisationUnit = organisationUnit;
        mUserAccount = userAccount;
        mType = required(type, "Type is required");
    }

    public static Survey createNewConnectSurvey(Program program,
                                                UserAccount userAccount) {
        Survey survey = new Survey(program, null, userAccount, Constants.SURVEY_NO_TYPE);
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

    public List<Question> getQuestions() {
        return questions;
    }

    @Override
    public String toString() {
        return "Survey{" +
                "id=" + id +
                ", status=" + status +
                ", mSurveyAnsweredRatio=" + mSurveyAnsweredRatio +
                ", mSurveyDate=" + mSurveyDate +
                ", mProgram=" + mProgram +
                ", mOrganisationUnit=" + mOrganisationUnit +
                ", mUserAccount=" + mUserAccount +
                ", mType=" + mType +
                ", questions=" + questions +
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
        if (mType != survey.mType) return false;
        if (mSurveyAnsweredRatio != null ? !mSurveyAnsweredRatio.equals(survey.mSurveyAnsweredRatio)
                : survey.mSurveyAnsweredRatio != null) {
            return false;
        }
        if (mSurveyDate != null ? !mSurveyDate.equals(survey.mSurveyDate)
                : survey.mSurveyDate != null) {
            return false;
        }
        if (mProgram != null ? !mProgram.equals(survey.mProgram) : survey.mProgram != null) {
            return false;
        }
        if (mOrganisationUnit != null ? !mOrganisationUnit.equals(survey.mOrganisationUnit)
                : survey.mOrganisationUnit != null) {
            return false;
        }
        if (mUserAccount != null ? !mUserAccount.equals(survey.mUserAccount)
                : survey.mUserAccount != null) {
            return false;
        }
        return questions != null ? questions.equals(survey.questions) : survey.questions == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + status;
        result = 31 * result + (mSurveyAnsweredRatio != null ? mSurveyAnsweredRatio.hashCode() : 0);
        result = 31 * result + (mSurveyDate != null ? mSurveyDate.hashCode() : 0);
        result = 31 * result + (mProgram != null ? mProgram.hashCode() : 0);
        result = 31 * result + (mOrganisationUnit != null ? mOrganisationUnit.hashCode() : 0);
        result = 31 * result + (mUserAccount != null ? mUserAccount.hashCode() : 0);
        result = 31 * result + mType;
        result = 31 * result + (questions != null ? questions.hashCode() : 0);
        return result;
    }

    public static final class Builder {
        private long id;
        private int status;
        private SurveyAnsweredRatio mSurveyAnsweredRatio;
        private Date mSurveyDate;
        private Program mProgram;
        private OrganisationUnit mOrganisationUnit;
        private UserAccount mUserAccount;
        private int mType;
        private List<Question> questions;

        public Builder() {
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder surveyAnsweredRatio(SurveyAnsweredRatio surveyAnsweredRatio) {
            mSurveyAnsweredRatio = surveyAnsweredRatio;
            return this;
        }

        public Builder surveyDate(Date surveyDate) {
            mSurveyDate = surveyDate;
            return this;
        }

        public Builder program(Program program) {
            mProgram = program;
            return this;
        }

        public Builder organisationUnit(OrganisationUnit organisationUnit) {
            mOrganisationUnit = organisationUnit;
            return this;
        }

        public Builder usserAccount(UserAccount userAccount) {
            mUserAccount = userAccount;
            return this;
        }

        public Builder type(int type) {
            mType = type;
            return this;
        }

        public Builder questions(List<Question> questions) {
            this.questions = questions;
            return this;
        }

        public Survey build() {
            return new Survey(id,
                    status,
                    mSurveyAnsweredRatio,
                    mSurveyDate,
                    mProgram,
                    mOrganisationUnit,
                    mUserAccount,
                    mType,
                    questions);
        }
    }
}
