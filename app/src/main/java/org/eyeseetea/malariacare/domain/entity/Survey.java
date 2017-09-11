package org.eyeseetea.malariacare.domain.entity;

import java.util.Date;

public class Survey {
    private long id;
    private int status;
    private SurveyAnsweredRatio mSurveyAnsweredRatio;
    private Date mSurveyDate;

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

    public void setSurveyAnsweredRatio(
            SurveyAnsweredRatio surveyAnsweredRatio) {
        mSurveyAnsweredRatio = surveyAnsweredRatio;
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
}
