package org.eyeseetea.malariacare.layout.adapters.survey.strategies;


import static org.eyeseetea.malariacare.data.database.utils.Session.getMalariaSurvey;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Treatment;
import org.eyeseetea.malariacare.domain.entity.TreatmentQueries;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.listeners.question.QuestionAnswerChangedListener;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.GradleVariantConfig;
import org.eyeseetea.malariacare.views.question.IQuestionView;
import org.eyeseetea.malariacare.views.question.multiquestion.NumberRadioButtonMultiquestionView;
import org.eyeseetea.malariacare.views.question.singlequestion
        .DynamicStockImageRadioButtonSingleQuestionView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DynamicTabAdapterStrategy implements IDynamicTabAdapterStrategy {

    DynamicTabAdapter mDynamicTabAdapter;
    /**
     * Added to save the dose by a quetion id when questions are dynamic treatment questions
     */
    HashMap<Long, Float> doseByQuestion;

    public DynamicTabAdapterStrategy(DynamicTabAdapter dynamicTabAdapter) {
        this.mDynamicTabAdapter = dynamicTabAdapter;
    }

    @Override
    public boolean HasQuestionImageVisibleInHeader(Integer output) {
        return output != Constants.SWITCH_BUTTON && output != Constants.QUESTION_LABEL
                && output != Constants.RADIO_GROUP_HORIZONTAL && output != Constants.REMINDER
                && output != Constants.IMAGE_RADIO_GROUP_NO_DATAELEMENT;
    }

    @Override
    public void initSurveys(boolean readOnly) {
        if (readOnly) {
            Survey malariaSurvey = Session.getMalariaSurvey();
            Session.setStockSurvey(
                    TreatmentQueries.getStockSurveyWithEventDate(malariaSurvey.getEventDate()));
        }
    }

    public List<Question> addAdditionalQuestions(int tabType, List<Question> screenQuestions) {
        if (tabType == Constants.TAB_DYNAMIC_TREATMENT) {
            Treatment treatment = new Treatment(Session.getMalariaSurvey(),
                    Session.getStockSurvey());
            if (treatment.hasTreatment()) {
                screenQuestions = treatment.getQuestions();
                doseByQuestion = treatment.getDoseByQuestion();
            } else {
                screenQuestions = treatment.getNoTreatmentQuestions();
            }

        }
        return screenQuestions;
    }

    @Override
    public void instanceOfSingleQuestion(IQuestionView questionView, Question screenQuestion) {

        if (questionView instanceof NumberRadioButtonMultiquestionView) {
            if (doseByQuestion != null) {
                ((NumberRadioButtonMultiquestionView) questionView).setDose(
                        doseByQuestion.get(screenQuestion.getId_question()));
            }
            ((NumberRadioButtonMultiquestionView) questionView).setQuestion(screenQuestion);
            ((NumberRadioButtonMultiquestionView) questionView).setOptions(
                    screenQuestion.getAnswer().getOptions());
        }
    }

    @Override
    public void instanceOfMultiQuestion(IQuestionView questionView, Question screenQuestion) {

        if (questionView instanceof NumberRadioButtonMultiquestionView) {
            if (doseByQuestion != null) {
                ((NumberRadioButtonMultiquestionView) questionView).setDose(
                        doseByQuestion.get(screenQuestion.getId_question()));
            }
            ((NumberRadioButtonMultiquestionView) questionView).setQuestion(screenQuestion);
            ((NumberRadioButtonMultiquestionView) questionView).setOptions(
                    screenQuestion.getAnswer().getOptions());
        }
    }

    @Override
    public void renderParticularSurvey(Question screenQuestion, Survey survey,
            IQuestionView questionView) {

        if (isDynamicStockQuestion(screenQuestion)) {
            Treatment treatment = new Treatment(getMalariaSurvey(),
                    Session.getStockSurvey());
            if (treatment.hasTreatment()) {
                org.eyeseetea.malariacare.data.database.model.Treatment dbTreatment =
                        treatment.getTreatment();
                Question actAnsweredNo = treatment.getACTQuestionAnsweredNo();
                screenQuestion.setAnswer(treatment.getACTOptions(dbTreatment));
                ((DynamicStockImageRadioButtonSingleQuestionView) questionView).setOptionDose(
                        treatment.getOptionDose(dbTreatment));
            }
            ((DynamicStockImageRadioButtonSingleQuestionView) questionView).setQuestion(
                    screenQuestion);
            ((DynamicStockImageRadioButtonSingleQuestionView) questionView).setOptions(
                    screenQuestion.getAnswer().getOptions());
            //Getting the question to put the correct values on it
            ArrayList<Question> questions = new ArrayList<>();
            for (Option option : screenQuestion.getAnswer().getOptions()) {
                Question question = Question.findByID(option.getId_option());
                if (question != null) {
                    questions.add(question);
                }
            }
            survey.getValuesFromDB();
            for (Question question : questions) {
                Value valueStock = question.getValueBySession();
                questionView.setValue(valueStock);
            }
        }
    }

    public boolean isDynamicStockQuestion(Question screenQuestion) {
        if (screenQuestion.getUid() != null) {
            return screenQuestion.getUid().equals(
                    PreferencesState.getInstance().getContext().getString(
                            R.string.dynamicStockQuestionUID));
        }
        return false;
    }

    @Override
    public boolean isMultiQuestionByVariant(int tabType) {
        return tabType == Constants.TAB_DYNAMIC_TREATMENT;
    }

    @Override
    public void configureAnswerChangedListener(DynamicTabAdapter dynamicTabAdapter,
            IQuestionView questionView) {
        if (questionView instanceof DynamicStockImageRadioButtonSingleQuestionView) {
            ((DynamicStockImageRadioButtonSingleQuestionView) questionView)
                    .setOnAnswerChangedListener(
                            new QuestionAnswerChangedListener(dynamicTabAdapter,
                                    !GradleVariantConfig.isButtonNavigationActive()));
            ((DynamicStockImageRadioButtonSingleQuestionView) questionView)
                    .setOnAnswerOptionChangedListener(
                            new QuestionAnswerChangedListener(dynamicTabAdapter,
                                    !GradleVariantConfig.isButtonNavigationActive()));
        } else if (questionView instanceof NumberRadioButtonMultiquestionView) {
            ((NumberRadioButtonMultiquestionView) questionView).setOnAnswerChangedListener(
                    new QuestionAnswerChangedListener(dynamicTabAdapter,
                            !GradleVariantConfig.isButtonNavigationActive()));
            ((NumberRadioButtonMultiquestionView) questionView).setOnAnswerOptionChangedListener(
                    new QuestionAnswerChangedListener(dynamicTabAdapter,
                            !GradleVariantConfig.isButtonNavigationActive()));
        }
    }
}
