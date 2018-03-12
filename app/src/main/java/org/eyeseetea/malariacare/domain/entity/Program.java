package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class Program {
    private String code;
    private String id;

    public Program() {
    }

    public Program(String code, String id) {
        this.id = required(id,"ID is required");
        this.code = required(code,"Code is required");
    }

    public String getCode() {
        return code;
    }

    public String getId() {
        return id;
    }
}
