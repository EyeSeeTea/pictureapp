package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class Language {

    private String code;
    private String name;

    public Language(String code, String name) {
        this.code = required(code,"Code is required");
        this.name = required(name,"Name is required");
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
