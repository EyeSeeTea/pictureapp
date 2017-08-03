package org.eyeseetea.malariacare.layout.adapters.survey.strategies;


import static org.eyeseetea.malariacare.data.database.utils.Session.getMalariaSurveyDB;

import android.view.View;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
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

public class DynamicTabAdapterStrategy extends ADynamicTabAdapterStrategy {
    /**
     * Added to save the dose by a quetion id when questions are dynamic treatment questions
     */
    HashMap<Long, Float> doseByQuestion;

    public DynamicTabAdapterStrategy(DynamicTabAdapter dynamicTabAdapter) {
        super(dynamicTabAdapter);
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
            SurveyDB malariaSurvey = Session.getMalariaSurveyDB();
            Session.setStockSurveyDB(
                    TreatmentQueries.getStockSurveyWithEventDate(malariaSurvey.getEventDate()));
        }
    }

    public List<QuestionDB> addAdditionalQuestions(int tabType, List<QuestionDB> screenQuestionDBs) {
        if (tabType == Constants.TAB_DYNAMIC_TREATMENT) {
            Treatment treatment = new Treatment(Session.getMalariaSurveyDB(),
                    Session.getStockSurveyDB());
            if (treatment.hasTreatment()) {
                screenQuestionDBs = treatment.getQuestionDBs();
                doseByQuestion = treatment.getDoseByQuestion();
            } else {
                screenQuestionDBs = treatment.getNoTreatmentQuestions();
            }

        }
        return screenQuestionDBs;
    }

    @Override
    public void instanceOfSingleQuestion(IQuestionView questionView, QuestionDB screenQuestionDB) {

        if (questionView instanceof NumberRadioButtonMultiquestionView) {
            if (doseByQuestion != null) {
                ((NumberRadioButtonMultiquestionView) questionView).setDose(
                        doseByQuestion.get(screenQuestionDB.getId_question()));
            }
            ((NumberRadioButtonMultiquestionView) questionView).setQuestionDB(screenQuestionDB);
            ((NumberRadioButtonMultiquestionView) questionView).setOptions(
                    screenQuestionDB.getAnswerDB().getOptionDBs());
        }
    }

    @Override
    public void instanceOfMultiQuestion(IQuestionView questionView, QuestionDB screenQuestionDB) {

        if (questionView instanceof NumberRadioButtonMultiquestionView) {
            if (doseByQuestion != null) {
                ((NumberRadioButtonMultiquestionView) questionView).setDose(
                        doseByQuestion.get(screenQuestionDB.getId_question()));
            }
            ((NumberRadioButtonMultiquestionView) questionView).setQuestionDB(screenQuestionDB);
            ((NumberRadioButtonMultiquestionView) questionView).setOptions(
                    screenQuestionDB.getAnswerDB().getOptionDBs());
        }
    }

    @Override
    public void renderParticularSurvey(QuestionDB screenQuestionDB, SurveyDB survey,
            IQuestionView questionView) {

        if (isDynamicStockQuestion(screenQuestionDB)) {
            Treatment treatment = new Treatment(getMalariaSurveyDB(),
                    Session.getStockSurveyDB());
            if (treatment.hasTreatment()) {
                org.eyeseetea.malariacare.data.database.model.TreatmentDB dbTreatment =
                        treatment.getTreatment();
                QuestionDB actAnsweredNo = treatment.getACTQuestionAnsweredNo();
                screenQuestionDB.setAnswer(treatment.getACTOptions(dbTreatment));
                ((DynamicStockImageRadioButtonSingleQuestionView) questionView).setOptionDose(
                        treatment.getOptionDose(dbTreatment));
            }
            ((DynamicStockImageRadioButtonSingleQuestionView) questionView).setQuestionDB(
                    screenQuestionDB);
            ((DynamicStockImageRadioButtonSingleQuestionView) questionView).setOptions(
                    screenQuestionDB.getAnswerDB().getOptionDBs());
            //Getting the question to put the correct values on it
            ArrayList<QuestionDB> questionDBs = new ArrayList<>();
            for (OptionDB option : screenQuestionDB.getAnswerDB().getOptionDBs()) {
                QuestionDB questionDB = QuestionDB.findByID(option.getId_option());
                if (questionDB != null) {
                    questionDBs.add(questionDB);
                }
            }
            survey.getValuesFromDB();
            for (QuestionDB questionDB : questionDBs) {
                ValueDB valueStock = questionDB.getValueBySession();
                questionView.setValue(valueStock);
            }
        }
    }

    public boolean isDynamicStockQuestion(QuestionDB screenQuestionDB) {
        if (screenQuestionDB.getUid() != null) {
            return screenQuestionDB.getUid().equals(
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

    @Override
    public void addScrollToSwipeTouchListener(View rowView) {
    }
}
