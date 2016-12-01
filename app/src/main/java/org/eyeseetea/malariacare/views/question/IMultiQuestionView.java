package org.eyeseetea.malariacare.views.question;

public interface IMultiQuestionView {
    void setHeader(String headerValue);

    void setImage(String path);

    boolean hasError();
}
