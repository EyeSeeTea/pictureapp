package org.eyeseetea.malariacare.data.sync.importer.poeditor.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Language {

    public String name;
    public String code;
    public Date updated;
}
