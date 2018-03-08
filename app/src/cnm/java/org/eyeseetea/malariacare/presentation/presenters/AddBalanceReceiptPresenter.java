package org.eyeseetea.malariacare.presentation.presenters;

import android.util.Log;

import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.usecase.CreateSurveyUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetQuestionsByProgramUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveSurveyUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveValueUseCase;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

public class AddBalanceReceiptPresenter {

    AddBalanceReceiptView mView;
    private CreateSurveyUseCase mCreateSurveyUseCase;
    private GetQuestionsByProgramUseCase mGetQuestionsByProgramUseCase;
    private SaveValueUseCase mSaveValueUseCase;
    private SaveSurveyUseCase mSaveSurveyUseCase;
    private Survey mSurvey;
    public AddBalanceReceiptPresenter(
            CreateSurveyUseCase createSurveyUseCase,
            GetQuestionsByProgramUseCase getQuestionsByProgramUseCase,
            SaveValueUseCase saveValueUseCase, SaveSurveyUseCase saveSurveyUseCase) {
        mCreateSurveyUseCase = createSurveyUseCase;
        mGetQuestionsByProgramUseCase = getQuestionsByProgramUseCase;
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
                Log.d(this.getClass().getName(), "Survey created id" + survey.getId());
                mGetQuestionsByProgramUseCase.execute(
                        new GetQuestionsByProgramUseCase.Callback() {

                            @Override
                            public void onGetQuestions(List<Question> questions) {
                                if (mView != null) {
                                    mView.showQuestions(questions);
                                }
                            }
                        }, programUID);
            }

            @Override
            public void onErrorCreatingSurvey(Throwable e) {
                if (mView != null) {
                    mView.showErrorMessage(e.getMessage());
                }
            }
        }, programUID, surveyType);

    }

    public void onQuestionAnswerTextChange(String questionUID, String value) {
        mSaveValueUseCase.execute(new SaveValueUseCase.Callback() {
            @Override
            public void onValueSaved(Value value) {
                Log.d(this.getClass().getName(), "Value saved" + value.getQuestionUId());
            }
        }, mSurvey.getId(), new Value(value, questionUID));
    }

    public void onCompletedSurvey() {
        mSurvey.setStatus(Constants.SURVEY_COMPLETED);
        mSaveSurveyUseCase.execute(mSurvey, new SaveSurveyUseCase.Callback() {
            @Override
            public void onSurveySaved() {
                Log.d(this.getClass().getName(), "Survey completed saved" + mSurvey.getId());
            }
        });
        mView.closeFragment();
    }

    public void detachView() {
        mView = null;
    }

    public interface AddBalanceReceiptView {
        void showQuestions(List<Question> questions);

        void showErrorMessage(String message);

        void closeFragment();
    }

}
