package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.domain.exception.LoadingNavigationControllerException;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;

public class SplashActivityStrategy extends ASplashActivityStrategy {
    public SplashActivityStrategy(Activity mActivity) {
        super(mActivity);
    }

    @Override
    public void finishAndGo() {

    }

    @Override
    public void initPullFilters(PullFilters pullFilters) {
        pullFilters.setAutoConfig(true);
    }

    @Override
    public void executePull(PullUseCase pullUseCase, PullFilters pullFilters) {
        pullUseCase.execute(pullFilters, new PullUseCase.Callback() {
            @Override
            public void onComplete() {
                Log.d(this.getClass().getSimpleName(), "pull complete");
                try {
                    NavigationBuilder.getInstance().buildController(TabDB.getFirstTab());
                    SplashActivityStrategy.this.finishAndGo(DashboardActivity.class);
                } catch (LoadingNavigationControllerException ex) {
                    onError(ex.getMessage());
                }
            }

            @Override
            public void onStep(PullStep step) {
                Log.d(this.getClass().getSimpleName(), step.toString());
            }

            @Override
            public void onError(String message) {
                Log.e(this.getClass().getSimpleName(), message);
                SplashActivityStrategy.this.showErrorAutoConfiguration();
            }

            @Override
            public void onNetworkError() {
                Log.e(this.getClass().getSimpleName(), "Network Error");
                SplashActivityStrategy.this.showErrorAutoConfiguration();
            }

            @Override
            public void onPullConversionError() {
                Log.e(this.getClass().getSimpleName(), "Pull Conversion Error");
                SplashActivityStrategy.this.showErrorAutoConfiguration();
            }

            @Override
            public void onCancel() {
                Log.e(this.getClass().getSimpleName(), "Pull oncancel");
                SplashActivityStrategy.this.showErrorAutoConfiguration();
            }
        });
    }


    private void showErrorAutoConfiguration() {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.survey_info_exit)
                .setMessage(R.string.error_auto_configuration)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        SplashActivityStrategy.super.finishAndGo(DashboardActivity.class);
                    }
                }).create().show();
    }

}