package org.eyeseetea.malariacare.presentation.presenters;


import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IValueRepository;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.usecase.CreateSurveyUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetQuestionsByProgramUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveSurveyUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveValueUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

public class AddBalanceReceiptPresenter {

    AddBalanceReceiptView mView;
    private CreateSurveyUseCase mCreateSurveyUseCase;
    private GetQuestionsByProgramUseCase mGetQuestionsByProgramUseCase;
    private SaveSurveyUseCase mSaveSurveyUseCase;
    private IValueRepository mValueRepository;
    private Survey mSurvey;
    public AddBalanceReceiptPresenter(
            CreateSurveyUseCase createSurveyUseCase,
            GetQuestionsByProgramUseCase getQuestionsByProgramUseCase,
            IValueRepository valueRepository,
            SaveSurveyUseCase saveSurveyUseCase) {
        mValueRepository = valueRepository;
        mCreateSurveyUseCase = createSurveyUseCase;
        mGetQuestionsByProgramUseCase = getQuestionsByProgramUseCase;
        mSaveSurveyUseCase = saveSurveyUseCase;
    }

    public void attachView(AddBalanceReceiptView addBalanceReceiptView, final String programUID,
            int surveyType) {
        mView = addBalanceReceiptView;
        mCreateSurveyUseCase.execute(new CreateSurveyUseCase.Callback() {
            @Override
            public void onCreateSurvey(Survey survey) {
                mSurvey = survey;
                mGetQuestionsByProgramUseCase.execute(
                        new GetQuestionsByProgramUseCase.Callback() {

                            @Override
                            public void onGetQuestions(List<Question> questions) {
                                if (mView != null) {
                                    mView.showQuestions(questions,"0");
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
        String valueToSave = value.isEmpty() ? "0" : value;
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        SaveValueUseCase saveValueUseCase = new SaveValueUseCase(asyncExecutor, mainExecutor,
                mValueRepository);
        saveValueUseCase.execute(new SaveValueUseCase.Callback() {
            @Override
            public void onValueSaved(Value value) {
            }
        }, mSurvey.getId(), new Value(valueToSave, questionUID));
    }

    public void onCompletedSurvey() {
        mSurvey.setStatus(Constants.SURVEY_COMPLETED);
        mSaveSurveyUseCase.execute(mSurvey, new SaveSurveyUseCase.Callback() {
            @Override
            public void onSurveySaved(Survey survey) {

            }
        });
        mView.closeFragment();
    }

    public void detachView() {
        mView = null;
    }

    public interface AddBalanceReceiptView {
        void showQuestions(List<Question> questions,String defValue);

        void showErrorMessage(String message);

        void closeFragment();
    }

}
