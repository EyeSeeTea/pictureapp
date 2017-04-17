package org.eyeseetea.malariacare.layout.adapters.survey.strategies;


import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.views.question.IQuestionView;

import java.util.List;

public interface IDynamicTabAdapterStrategy {
    boolean HasQuestionImageVisibleInHeader(Integer output);

    void initSurveys(boolean readOnly);

    List<Question> addAdditionalQuestions(int tabType, List<Question> screenQuestions);

    void instanceOfSingleQuestion(IQuestionView questionView, Question screenQuestion);

    void instanceOfMultiQuestion(IQuestionView questionView, Question screenQuestion);

    void renderParticularSurvey(Question screenQuestion, Survey survey, IQuestionView questionView);

    boolean isMultiQuestionByVariant(int tabType);

    void configureAnswerChangedListener(DynamicTabAdapter dynamicTabAdapter,
            IQuestionView questionView);
}
