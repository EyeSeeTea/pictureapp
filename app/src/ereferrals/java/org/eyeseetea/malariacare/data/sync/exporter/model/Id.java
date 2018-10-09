package org.eyeseetea.malariacare.data.sync.exporter.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Id {
    String id;

    public Id(String uid){
        id=uid;
    }

    public String getId() {
        return id;
    }
}
