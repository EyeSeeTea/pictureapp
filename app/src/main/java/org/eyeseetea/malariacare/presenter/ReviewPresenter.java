package org.eyeseetea.malariacare.presenter;

import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.usecase.GetReviewValuesBySurveyIdUseCase;

import java.util.List;

public class ReviewPresenter {

    public interface ReviewView {
        void showValues(List<Value> values);

        void initListView();

        void navigateToQuestion(String uId);
    }

    ReviewView view;
    private GetReviewValuesBySurveyIdUseCase mGetReviewValuesBySurveyIdUseCase;

    public ReviewPresenter(
            GetReviewValuesBySurveyIdUseCase getReviewValuesBySurveyIdUseCase) {
        mGetReviewValuesBySurveyIdUseCase = getReviewValuesBySurveyIdUseCase;
    }

    public void attachView(ReviewView reviewView, Long surveyId) {
        this.view = reviewView;
        mGetReviewValuesBySurveyIdUseCase.execute(
                new GetReviewValuesBySurveyIdUseCase.Callback() {
            @Override
            public void onGetValues(List<Value> values) {
                if (view != null) {
                    view.showValues(values);
                    view.initListView();
                }
            }
                }, surveyId);
    }

    public void detachView() {
        view = null;
    }

    public void onClickOnValue(String UId) {
        view.navigateToQuestion(UId);
    }
}
