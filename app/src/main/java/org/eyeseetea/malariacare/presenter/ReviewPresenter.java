package org.eyeseetea.malariacare.presenter;

import org.eyeseetea.malariacare.data.database.utils.Session;
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

    public void attachView(ReviewView reviewView) {
        this.view = reviewView;
        mGetReviewValuesBySurveyIdUseCase.execute(
                new GetReviewValuesBySurveyIdUseCase.Callback() {
            @Override
            public void onGetValues(List<Value> values) {
                view.showValues(values);
                view.initListView();
            }
        }, Session.getMalariaSurvey().getId_survey());
    }

    public void onClickOnValue(String UId) {
        view.navigateToQuestion(UId);
    }
}
