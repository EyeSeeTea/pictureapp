package org.eyeseetea.malariacare.data.sync.importer.poeditor.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
@SuppressWarnings("WeakerAccess")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Term {
    public String term;
    public Date created;
    public Translation translation;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Translation{
        public String content;
    }
}
