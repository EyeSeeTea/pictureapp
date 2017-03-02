package org.eyeseetea.malariacare;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.config.EyeSeeTeaGeneratedDatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.eyeseetea.malariacare.data.database.utils.LocationMemory;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.strategies.SplashActivityStrategy;
import org.hisp.dhis.client.sdk.android.api.D2;

import io.fabric.sdk.android.Fabric;

public class SplashScreenActivity extends Activity {


    private static final String TAG = ".SplashScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AsyncInitApplication asyncInitApplication = new AsyncInitApplication(this);
        asyncInitApplication.execute((Void) null);
    }

    private void init() {
        Fabric.with(this, new Crashlytics());
        LocationMemory.getInstance().init(getApplicationContext());

        D2.init(this);
        FlowConfig flowConfig = new FlowConfig
                .Builder(this)
                .addDatabaseHolder(EyeSeeTeaGeneratedDatabaseHolder.class)
                .build();
        FlowManager.init(flowConfig);
        SdkQueries.createDBIndexes();

        //TODO: after mega merge
        //PostMigration.launchPostMigration();

        if (!BuildConfig.multiuser) {
            Log.i(TAG, "Pull on SplashScreen ...");

            PullController pullController = new PullController(
                    getApplication().getApplicationContext());

            PullUseCase pullUseCase = new PullUseCase(pullController);

            PullFilters pullFilters = new PullFilters();
            pullFilters.setDemo(true);

            pullUseCase.execute(pullFilters, new PullUseCase.Callback() {
                @Override
                public void onComplete() {
                    Log.d(this.getClass().getSimpleName(), "pull complete");
                }

                @Override
                public void onStep(PullStep step) {
                    Log.d(this.getClass().getSimpleName(), step.toString());
                }

                @Override
                public void onError(String message) {
                    Log.e(this.getClass().getSimpleName(), message);
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

    }

    public class AsyncInitApplication extends AsyncTask<Void, Void, Exception> {
        Activity activity;

        AsyncInitApplication(Activity activity) {
            this.activity = activity;
        }


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Exception doInBackground(Void... params) {
            init();
            return null;
        }

        @Override
        protected void onPostExecute(final Exception exception) {
            //Error
            SplashActivityStrategy splashActivityStrategy = new SplashActivityStrategy(activity);
            splashActivityStrategy.finishAndGo();
        }
    }
}
