package org.eyeseetea.malariacare.domain.entity;

public class Value {
    String value;
    String internationalizatedCode;
    String backgroundColor;
    String questionUId;

    public Value(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public String getInternationalizedCode() {
        return internationalizatedCode;
    }

    public void setInternationalizedCode(String internationalizatedCode) {
        this.internationalizatedCode = internationalizatedCode;
    }
}
