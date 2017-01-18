package org.eyeseetea.malariacare;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.config.EyeSeeTeaGeneratedDatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.eyeseetea.malariacare.database.PostMigration;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.utils.LocationMemory;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.usecase.ALoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.InitUseCase;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.data.sdk.SdkQueries;
import org.eyeseetea.malariacare.views.TypefaceCache;
import org.hisp.dhis.client.sdk.android.api.D2;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;

/**
 * Created by idelcano on 12/12/2016.
 */

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
        TypefaceCache.getInstance().init(getApplicationContext());

        D2.init(this);
        FlowConfig flowConfig = new FlowConfig
                .Builder(this)
                .addDatabaseHolder(EyeSeeTeaGeneratedDatabaseHolder.class)
                .build();
        FlowManager.init(flowConfig);
        SdkQueries.createDBIndexes();
        PostMigration.launchPostMigration();

        //Get maximum total of questions
        if (!Tab.isEmpty()) {
            Session.setMaxTotalQuestions(Program.getMaxTotalQuestions());
        }

        try {
            if (!BuildConfig.multiuser) {
                Log.i(TAG, "Creating demo login from dashboard ...");
                LoginUseCase loginUseCase = new LoginUseCase(this);

                Credentials demoCrededentials = Credentials.createDemoCredentials();

                loginUseCase.execute(demoCrededentials, new ALoginUseCase.Callback() {
                    @Override
                    public void onLoginSuccess() {
                        Log.d(TAG, "Login Success");
                    }

                    @Override
                    public void onLoginError(String message) {
                        Log.d(TAG, message);
                    }
                });
            }

            PopulateDB.initDataIfRequired(getAssets());
        } catch (IOException exception) {
            Log.e("LoginActivity", "ERROR: DB not loaded");
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
            InitUseCase initUseCase = new InitUseCase(activity);
            initUseCase.finishAndGo();
        }
    }
}
