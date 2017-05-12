package org.eyeseetea.malariacare.domain.entity;

public class Program {
    private String code;
    private String id;

    public Program() {
    }

    public Program(String code, String id) {
        this.code = code;
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
