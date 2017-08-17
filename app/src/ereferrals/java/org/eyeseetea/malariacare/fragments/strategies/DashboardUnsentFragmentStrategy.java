package org.eyeseetea.malariacare.fragments.strategies;

import android.view.View;

import org.eyeseetea.malariacare.data.database.datasources.CanAddSurveysLocalDataSource;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICanAddSurveysRepository;
import org.eyeseetea.malariacare.domain.usecase.CanAddNewSurveyButtonUseCase;

public class DashboardUnsentFragmentStrategy extends ADashboardUnsentFragmentStrategy {

    @Override
    public void initFooter(final View footer) {
        ICanAddSurveysRepository canAddSurveysLocalDataSource = new CanAddSurveysLocalDataSource();
        CanAddNewSurveyButtonUseCase canAddNewSurveyButtonUseCase =
                new CanAddNewSurveyButtonUseCase(canAddSurveysLocalDataSource);
        canAddNewSurveyButtonUseCase.execute(new CanAddNewSurveyButtonUseCase.Callback() {
            @Override
            public void canAddNewSurveyButton(boolean canAddNewSurvey) {
                int visibility = canAddNewSurvey ? View.VISIBLE : View.GONE;
                footer.setVisibility(visibility);
            }
        });
    }
}
