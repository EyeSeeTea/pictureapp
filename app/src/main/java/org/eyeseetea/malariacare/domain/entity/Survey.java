package org.eyeseetea.malariacare.domain.entity;

import java.util.Date;

public class Survey {
    private long id;
    private int status;
    private Date eventDate;
    private SurveyAnsweredRatio mSurveyAnsweredRatio;

    public Survey() {
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

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Survey survey = (Survey) o;

        if (id != survey.id) return false;
        if (status != survey.status) return false;
        return eventDate != null ? eventDate.equals(survey.eventDate) : survey.eventDate == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + status;
        result = 31 * result + (eventDate != null ? eventDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Survey{" +
                "id=" + id +
                ", status=" + status +
                ", eventDate=" + eventDate +
                '}';
    }
}
