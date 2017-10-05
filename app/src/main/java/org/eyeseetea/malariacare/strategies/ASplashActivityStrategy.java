package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.domain.exception.LoadingNavigationControllerException;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;


public abstract class ASplashActivityStrategy {

    protected Activity activity;

    public ASplashActivityStrategy(Activity activity) {
        this.activity = activity;
    }

    public abstract void finishAndGo();

    protected void finishAndGo(Class<? extends Activity> activityClass) {
        activity.startActivity(new Intent(activity, activityClass));
        activity.finish();
    }
    public void initPullFilters(PullFilters pullFilters){
        pullFilters.setDemo(true);
    }

    public void executePull(PullUseCase pullUseCase, PullFilters pullFilters) {
        pullUseCase.execute(pullFilters, new PullUseCase.Callback() {
            @Override
            public void onComplete() {
                Log.d(this.getClass().getSimpleName(), "pull complete");
                try {
                    NavigationBuilder.getInstance().buildController(TabDB.getFirstTab());
                } catch (LoadingNavigationControllerException ex) {
                    onError(ex);
                }
            }

            @Override
            public void onStep(PullStep step) {
                Log.d(this.getClass().getSimpleName(), step.toString());
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(this.getClass().getSimpleName(), throwable.getMessage());
            }

            @Override
            public void onNetworkError() {
                Log.e(this.getClass().getSimpleName(), "Network Error");
            }

            @Override
            public void onPullConversionError() {
                Log.e(this.getClass().getSimpleName(), "Pull Conversion Error");
            }

            @Override
            public void onCancel() {
                Log.e(this.getClass().getSimpleName(), "Pull oncancel");
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            int[] grantResults) {

    }
}
