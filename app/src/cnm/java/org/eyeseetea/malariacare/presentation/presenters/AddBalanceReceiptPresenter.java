package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.usecase.CreateSurveyUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetQuestionsForProgramUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveSurveyUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveValueUseCase;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

public class AddBalanceReceiptPresenter {

    AddBalanceReceiptView mView;
    private CreateSurveyUseCase mCreateSurveyUseCase;
    private GetQuestionsForProgramUseCase mGetQuestionsForProgramUseCase;
    private SaveValueUseCase mSaveValueUseCase;
    private SaveSurveyUseCase mSaveSurveyUseCase;
    private Survey mSurvey;
    public AddBalanceReceiptPresenter(
            CreateSurveyUseCase createSurveyUseCase,
            GetQuestionsForProgramUseCase getQuestionsForProgramUseCase,
            SaveValueUseCase saveValueUseCase, SaveSurveyUseCase saveSurveyUseCase) {
        mCreateSurveyUseCase = createSurveyUseCase;
        mGetQuestionsForProgramUseCase = getQuestionsForProgramUseCase;
        mSaveValueUseCase = saveValueUseCase;
        mSaveSurveyUseCase = saveSurveyUseCase;
    }

    public void attachView(AddBalanceReceiptView addBalanceReceiptView, final String programUID,
            int surveyType) {
        mView = addBalanceReceiptView;
        mCreateSurveyUseCase.execute(new CreateSurveyUseCase.Callback() {
            @Override
            public void onCreateSurvey(Survey survey) {
                mSurvey = survey;
                mGetQuestionsForProgramUseCase.execute(
                        new GetQuestionsForProgramUseCase.Callback() {

                            @Override
                            public void onGetQuestions(List<Question> questions) {
                                mView.showQuestions(questions);
                            }
                        }, programUID);
            }

            @Override
            public void onErrorCreatingSurvey(Throwable e) {
                mView.showErrorMessage(e.getMessage());
            }
        }, programUID, surveyType);

    }

    public void onQuestionAnswerTextChange(String questionUID, String value) {
        mSaveValueUseCase.execute(new SaveValueUseCase.Callback() {
            @Override
            public void onValueSaved(Value value) {

            }
        }, mSurvey.getId(), new Value(value, questionUID));
    }

    public void onClompletedSurvey() {
        mSurvey.setStatus(Constants.SURVEY_COMPLETED);
        mSaveSurveyUseCase.execute(mSurvey, new SaveSurveyUseCase.Callback() {
            @Override
            public void onSurveySaved() {

            }
        });
    }

    public interface AddBalanceReceiptView {
        void showQuestions(List<Question> questions);

        void showErrorMessage(String message);
    }

}
