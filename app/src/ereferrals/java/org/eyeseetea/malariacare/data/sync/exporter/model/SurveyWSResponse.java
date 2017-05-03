package org.eyeseetea.malariacare.data.sync.exporter.model;

public class SurveyWSResponse {
    private String msg;
    private SurveyWSResponseData data;

    public SurveyWSResponse() {
    }

    public SurveyWSResponse(String msg,
            SurveyWSResponseData data) {
        this.msg = msg;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public SurveyWSResponseData getData() {
        return data;
    }

    public void setData(SurveyWSResponseData data) {
        this.data = data;
    }
}
