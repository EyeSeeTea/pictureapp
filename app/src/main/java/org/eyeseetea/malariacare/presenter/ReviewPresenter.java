package org.eyeseetea.malariacare.presenter;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.data.database.datasources.ValueLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IValueRepository;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.usecase.GetReviewValuesBySurveyIdUseCase;
import org.eyeseetea.malariacare.layout.adapters.dashboard.ReviewScreenAdapter;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

import java.util.List;

public class ReviewPresenter implements ReviewScreenAdapter.onClickListener {

    public interface ReviewView {
        void showValues(List<Value> values);

        void initListView();
    }

    ReviewView view;

    public ReviewPresenter() {
    }

    public void attachView(ReviewView reviewView) {
        this.view = reviewView;
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IValueRepository valueLocalDataSource = new ValueLocalDataSource();
        GetReviewValuesBySurveyIdUseCase getReviewValuesBySurveyIdUseCase =
                new GetReviewValuesBySurveyIdUseCase(mainExecutor, asyncExecutor,
                        valueLocalDataSource);
        getReviewValuesBySurveyIdUseCase.execute(new GetReviewValuesBySurveyIdUseCase.Callback() {
            @Override
            public void onGetValues(List<Value> values) {
                view.showValues(values);
                view.initListView();
            }
        }, Session.getMalariaSurvey().getId_survey());
    }

    @Override
    public void onClickOnValue(String UId) {
        DashboardActivity.dashboardActivity.hideReview(UId);
    }
}
