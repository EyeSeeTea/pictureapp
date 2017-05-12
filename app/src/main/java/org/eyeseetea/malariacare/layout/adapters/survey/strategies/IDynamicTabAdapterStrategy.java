package org.eyeseetea.malariacare.layout.adapters.survey.strategies;


public interface IDynamicTabAdapterStrategy {
    boolean HasQuestionImageVisibleInHeader(Integer output);
    void initSurveys(boolean readOnly);
}
