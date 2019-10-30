package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.usecase.GetSurveyByUidUseCase;

import java.util.List;

public class ReviewPresenter {

    public interface ReviewView {
        void showValues(List<Value> values);

        void initListView();

        void navigateToQuestion(String uId);
    }

    ReviewView view;
    private GetSurveyByUidUseCase getSurveyByUidUseCase;

    public ReviewPresenter(GetSurveyByUidUseCase getSurveyByUidUseCase) {
        this.getSurveyByUidUseCase = getSurveyByUidUseCase;
    }

    public void attachView(ReviewView reviewView, String surveyUId) {
        this.view = reviewView;
        getSurveyByUidUseCase.execute(surveyUId,
                new GetSurveyByUidUseCase.Callback() {
                    @Override
                    public void onSuccess(Survey survey) {
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
}
