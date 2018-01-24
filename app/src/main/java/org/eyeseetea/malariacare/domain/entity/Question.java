package org.eyeseetea.malariacare.domain.entity;


import java.util.List;

public class Question {
    private String code;
    private String name;
    private Type type;
    private boolean compulsory;
    private List<Option> options;

    public Question(String code, String name,
            Type type, boolean compulsory,
            List<Option> options) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.compulsory = compulsory;
        this.options = options;
    }

    public Question() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isCompulsory() {
        return compulsory;
    }

    public void setCompulsory(boolean compulsory) {
        this.compulsory = compulsory;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }


    public enum Type {
        SHORT_TEXT, PHONE, DROPDOWN_LIST, YEAR, DATE, INT, LONG_TEXT, POSITIVE_INT,
        PREGNANT_MONTH, RADIO_GROUP_HORIZONTAL, QUESTION_LABEL, SWITCH_BUTTON
    }
}
