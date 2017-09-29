package org.eyeseetea.malariacare;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import org.eyeseetea.malariacare.data.database.PostMigration;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
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
        setContentView(R.layout.activity_splash);
        AsyncInitApplication asyncInitApplication = new AsyncInitApplication(this);
        asyncInitApplication.execute((Void) null);
    }

    private void init() {
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
            splashActivityStrategy.initPullFilters(pullFilters);
            splashActivityStrategy.executePull(pullUseCase, pullFilters);
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
            splashActivityStrategy.finishAndGo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        splashActivityStrategy.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
