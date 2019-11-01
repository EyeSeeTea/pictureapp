package org.eyeseetea.malariacare.presentation.presenters;

import static org.eyeseetea.malariacare.utils.Constants.SURVEY_IN_PROGRESS;

import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.usecase.CompletionSurveyUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetSurveyByUidUseCase;
import org.eyeseetea.malariacare.factories.SurveyFactory;

import java.util.List;

public class ReviewPresenter {

    public interface ReviewView {
        void showValues(List<Value> values);

        void initListView();

        void navigateToQuestion(String uId);

        void exit(String surveyUid,boolean afterCompletion);
    }

    ReviewView view;
    private GetSurveyByUidUseCase getSurveyByUidUseCase;
    private CompletionSurveyUseCase completionSurveyUseCase;

    private Survey survey;

    public ReviewPresenter(GetSurveyByUidUseCase getSurveyByUidUseCase,
            CompletionSurveyUseCase completionSurveyUseCase) {
        this.getSurveyByUidUseCase = getSurveyByUidUseCase;
        this.completionSurveyUseCase = completionSurveyUseCase;
    }

    public void attachView(ReviewView reviewView, String surveyUId) {
        this.view = reviewView;
        getSurveyByUidUseCase.execute(surveyUId,
                new GetSurveyByUidUseCase.Callback() {
                    @Override
                    public void onSuccess(Survey survey) {
                        ReviewPresenter.this.survey = survey;
                        if (view != null) {
                            view.showValues(survey.getValues());
                            view.initListView();
                        }
                    }

                    @Override
                    public void onError(Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                });
    }

    public void detachView() {
        view = null;
    }

    public void onClickOnValue(String UId) {
        view.navigateToQuestion(UId);
    }

    public void ok() {
        if (survey.getStatus() == SURVEY_IN_PROGRESS) {
            sendSurvey();
        } else {
            if (view != null) {
                view.exit(survey.getUid(), false);
            }
        }
    }

    private void sendSurvey() {
        //TODO: This should be realized in the use case but
        // require uncouple SurveyAnsweredCalculation from DB and UI
        SurveyDB surveyDB = SurveyDB.findByUid(survey.getUid());
        surveyDB.updateSurveyStatus();

        CompletionSurveyUseCase completionSurveyUseCase =
                new SurveyFactory().getCompletionSurveyUseCase();

        completionSurveyUseCase.execute(survey.getUid(),
                new CompletionSurveyUseCase.CompletionSurveyCallback() {
                    @Override
                    public void CompletionSurveySuccess(Survey survey) {
                        //TODO: on the future when does not exists session reads, remove this
                        SurveyDB surveyDB = SurveyDB.findByUid(survey.getUid());
                        Session.setMalariaSurveyDB(surveyDB);

                        if (view != null) {
                            view.exit(survey.getUid(), true);
                        }
                    }

                    @Override
                    public void CompletionSurveyError(Exception e) {
                        System.out.println(e.getMessage());
                    }
                });
    }
}
