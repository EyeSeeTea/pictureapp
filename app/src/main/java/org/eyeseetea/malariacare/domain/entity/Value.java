package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

public class Value {
    String value;
    String internationalizatedName;
    String backgroundColor;
    String questionUId;
    String optionCode;

    public Value(String value) {
        this.value = value;
    }

    public Value(String value, String questionUId) {
        this.value = required(value, "Program is required");
        this.questionUId = required(questionUId, "Program is required");
    }

    public Value(String value, String questionUId, String optionCode) {
        this.value = required(value, "Program is required");
        this.questionUId = required(questionUId, "Program is required");
        this.optionCode = required(optionCode, "Program is required");
    }

    public String getValue() {
        return value;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getQuestionUId() {
        return questionUId;
    }

    public void setQuestionUId(String questionUId) {
        this.questionUId = questionUId;
    }

    public String getInternationalizedName() {
        return internationalizatedName;
    }

    public void setInternationalizedName(String internationalizatedName) {
        this.internationalizatedName = internationalizatedName;
    }

    public String getOptionCode() {
        return optionCode;
    }

    public void setOptionCode(String optionCode) {
        this.optionCode = optionCode;
    }
}
