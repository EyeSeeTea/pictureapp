package org.eyeseetea.malariacare.data.sync.importer.poeditor.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("WeakerAccess")
public final class TermsResult {
    public List<Term> terms;
}
