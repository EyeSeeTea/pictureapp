package org.eyeseetea.malariacare.strategies;

import android.content.Context;

import static org.eyeseetea.malariacare.utils.Constants.SURVEY_SENT;

import org.eyeseetea.malariacare.data.database.datasources.AuthDataSource;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.ValueDB;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Settings;
import org.eyeseetea.malariacare.domain.entity.intent.Auth;
import org.eyeseetea.malariacare.domain.usecase.GetAuthUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetSettingsUseCase;
import org.eyeseetea.malariacare.factories.SettingsFactory;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class SurveyFragmentStrategy {

    public static SurveyDB getRenderSurvey(QuestionDB screenQuestion) {
        return Session.getMalariaSurveyDB();
    }

    public static SurveyDB getSessionSurveyByQuestion(QuestionDB question) {
        return Session.getMalariaSurveyDB();
    }

    public static void saveValueDDlExtraOperations(ValueDB value, OptionDB optionDB, String uid) {

    }

    public static SurveyDB getSaveValuesDDLSurvey(QuestionDB question) {
        return Session.getMalariaSurveyDB();
    }

    public static void saveValuesText(ValueDB value, String answer,
            QuestionDB question, SurveyDB survey) {
        if (value != null && answer.isEmpty()) {
            removeValueText(value);
        } else {
            question.createOrSaveValue(answer, value, survey);
            for (QuestionDB propagateQuestion : question.getPropagationQuestions()) {
                propagateQuestion.createOrSaveValue(answer,
                        ValueDB.findValueFromDatabase(propagateQuestion.getId_question(),
                                survey), survey);
            }
        }
    }

    public static void removeValueText(ValueDB value) {
        if (value != null) value.delete();
    }

    public static void recursiveRemover(ValueDB value, OptionDB optionDB, QuestionDB question,
            SurveyDB survey) {
        if (!value.getOptionDB().equals(optionDB) && question.hasChildren()) {
            survey.removeChildrenValuesFromQuestionRecursively(question, false);
        }
    }

    public static List<QuestionDB> getCompulsoryNotAnsweredQuestions(QuestionDB question) {
        List<QuestionDB> questions = new ArrayList<>();
        if (question.getHeaderDB().getTabDB().getType().equals(Constants.TAB_MULTI_QUESTION)) {
            questions = question.getQuestionsByTab(question.getHeaderDB().getTabDB());
        } else {
            questions.add(question);
        }
        return questions;
    }

    public static int getNumRequired(int numRequired, QuestionDB localQuestion) {
        while (localQuestion.getSibling() != null) {
            if (localQuestion.isCompulsory()) {
                numRequired++;
            }
            localQuestion = localQuestion.getSibling();
        }
        if (!localQuestion.isCompulsory()) {
            numRequired--;
        }
        return numRequired;

    }

    public static void setSurveyAsSent(SurveyDB survey) {
        //Check surveys not in progress
        survey.setStatus(SURVEY_SENT);
        survey.save();
    }

    public void removeSurveysInSession() {
        Session.setMalariaSurveyDB(null);
    }

    public static void isSurveyCreatedFromOtherApp(final ASurveyFragmentStrategy.Callback callback, Context context){
        GetAuthUseCase getAuthUseCase = new GetAuthUseCase( new UIThreadExecutor(), new AsyncExecutor(), new AuthDataSource(context));
        getAuthUseCase.execute(new GetAuthUseCase.Callback() {
            @Override
            public void onGetAuth(Auth auth) {
                callback.loadIsSurveyCreatedInOtherApp(auth!=null);
            }
        });
    }

    public static void isSurveyJumpingActive (
            final ASurveyFragmentStrategy.GetSurveyJumpingCallback callback,
            Context context){
        GetSettingsUseCase getSettingsUseCase = new SettingsFactory().getSettingsUseCase(context);
        getSettingsUseCase.execute(new GetSettingsUseCase.Callback() {
            @Override
            public void onSuccess(Settings settings) {
                callback.onSuccess(settings.isSurveyJumpingActive());
            }
        });
    }
}
