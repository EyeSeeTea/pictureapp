package org.eyeseetea.malariacare.data.sync.exporter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveySimpleObject {
    String id;
    boolean existOnServer;
    String resultType;

    public SurveySimpleObject(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isExistOnServer() {
        return existOnServer;
    }

    public void setExistOnServer(boolean existOnServer) {
        this.existOnServer = existOnServer;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
}