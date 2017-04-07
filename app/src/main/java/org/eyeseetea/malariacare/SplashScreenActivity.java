package org.eyeseetea.malariacare;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.eyeseetea.malariacare.data.database.PostMigration;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.utils.LocationMemory;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.exception.LoadingNavigationControllerException;
import org.eyeseetea.malariacare.domain.exception.PostMigrationException;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.strategies.SplashActivityStrategy;
import org.eyeseetea.malariacare.utils.Utils;
import org.hisp.dhis.client.sdk.android.api.D2;

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
        LocationMemory.getInstance().init(getApplicationContext());

        D2.init(this);
        SdkQueries.createDBIndexes();
        //Added to execute a query in DB, because DBFLow doesn't do any migration until a query
        // is executed
        PopulateDB.initDBQuery();
        try {
            PostMigration.launchPostMigration();
        }catch (PostMigrationException e){
            new AlertDialog.Builder(this)
                    .setTitle(getApplicationContext().getString(R.string.error_message))
                    .setCancelable(false)
                    .setMessage(getApplicationContext().getString(R.string.db_migration_error))
                    .setNeutralButton(android.R.string.ok, null).create().show();
        }

        if (!BuildConfig.multiuser) {
            Log.i(TAG, "Pull on SplashScreen ...");

            PullController pullController = new PullController(
                    getApplication().getApplicationContext());
            IAsyncExecutor asyncExecutor = new AsyncExecutor();
            IMainExecutor mainExecutor = new UIThreadExecutor();

            PullUseCase pullUseCase = new PullUseCase(pullController, asyncExecutor, mainExecutor);

            PullFilters pullFilters = new PullFilters();
            pullFilters.setDemo(true);

            pullUseCase.execute(pullFilters, new PullUseCase.Callback() {
                @Override
                public void onComplete() {
                    Log.d(this.getClass().getSimpleName(), "pull complete");
                    try {
                        NavigationBuilder.getInstance().buildController(Tab.getFirstTab());
                    }catch (LoadingNavigationControllerException ex){
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

    public class AsyncInitApplication extends AsyncTask<Void, Void, Void> {
        Activity activity;

        AsyncInitApplication(Activity activity) {
            this.activity = activity;
        }


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            init();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            SplashActivityStrategy splashActivityStrategy = new SplashActivityStrategy(activity);
            splashActivityStrategy.finishAndGo();
        }
    }
}
