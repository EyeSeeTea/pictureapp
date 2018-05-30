package org.eyeseetea.malariacare;

import static org.eyeseetea.malariacare.BuildConfig.maxDaysForDeletingSentSurveys;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.eyeseetea.malariacare.data.database.PostMigration;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.exception.LanguagesDownloadException;
import org.eyeseetea.malariacare.domain.exception.PostMigrationException;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.strategies.SplashActivityStrategy;
import org.hisp.dhis.client.sdk.android.api.D2;

public class SplashScreenActivity extends Activity {


    private static final String TAG = ".SplashScreenActivity";
    private SplashActivityStrategy splashActivityStrategy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        splashActivityStrategy = new SplashActivityStrategy(this);
        if (splashActivityStrategy.canEnterApp()) {
            setContentView(R.layout.activity_splash);
            AsyncInitApplication asyncInitApplication = new AsyncInitApplication(this);
            asyncInitApplication.execute((Void) null);
        }
    }

    private void init() throws Exception {
        D2.init(this);
        SdkQueries.createDBIndexes();
        //Added to execute a query in DB, because DBFLow doesn't do any migration until a query
        // is executed
        PopulateDB.initDBQuery();
        try {
            PostMigration.launchPostMigration();
        } catch (PostMigrationException e) {
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
            splashActivityStrategy.initPullFilters(pullFilters);
            splashActivityStrategy.executePull(pullUseCase, pullFilters);
        }

        try {
            splashActivityStrategy.downloadLanguagesFromServer();
        } catch (Exception e) {
            Log.e(TAG, "Unable to download Languages From Server" + e.getMessage());
            e.printStackTrace();
            throw e;
        }


        if(BuildConfig.performMaintenanceTasks) {
            performMaintenanceTasks();
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
            try {
                init();
            } catch (Exception e) {
                if (e instanceof LanguagesDownloadException) {
                    showToast(R.string.error_downloading_languages, e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            splashActivityStrategy.finishAndGo();
        }
    }

    private void showToast(int titleResource, final Exception e) {
        final String title = getResources().getString(titleResource);
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), title + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        splashActivityStrategy.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void performMaintenanceTasks() {
        SurveyDB.deleteOlderSentSurveys(maxDaysForDeletingSentSurveys);
    }
}
