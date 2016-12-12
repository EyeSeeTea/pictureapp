package org.eyeseetea.malariacare;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.index.Index;

import org.eyeseetea.malariacare.database.PostMigration;
import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.Match$Table;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.QuestionOption;
import org.eyeseetea.malariacare.database.model.QuestionOption$Table;
import org.eyeseetea.malariacare.database.model.QuestionRelation;
import org.eyeseetea.malariacare.database.model.QuestionRelation$Table;
import org.eyeseetea.malariacare.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.database.model.QuestionThreshold$Table;
import org.eyeseetea.malariacare.database.model.Tab;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.model.Value$Table;
import org.eyeseetea.malariacare.database.utils.LocationMemory;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.usecase.InitUseCase;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.TypefaceCache;

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

    private void init() {
        Fabric.with(this, new Crashlytics());
        PreferencesState.getInstance().init(getApplicationContext());
        LocationMemory.getInstance().init(getApplicationContext());
        TypefaceCache.getInstance().init(getApplicationContext());

        FlowManager.init(this, "_EyeSeeTeaDB");
        createDBIndexes();
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

                loginUseCase.execute(demoCrededentials);
            }

            PopulateDB.initDataIfRequired(getAssets());
        } catch (IOException exception) {
            Log.e("LoginActivity", "ERROR: DB not loaded");
        }
    }


    private void createDBIndexes() {
        new Index<QuestionOption>(Constants.QUESTION_OPTION_QUESTION_IDX).on(QuestionOption.class,
                QuestionOption$Table.ID_QUESTION).enable();
        new Index<QuestionOption>(Constants.QUESTION_OPTION_MATCH_IDX).on(QuestionOption.class,
                QuestionOption$Table.ID_MATCH).enable();

        new Index<QuestionRelation>(Constants.QUESTION_RELATION_OPERATION_IDX).on(
                QuestionRelation.class, QuestionRelation$Table.OPERATION).enable();
        new Index<QuestionRelation>(Constants.QUESTION_RELATION_QUESTION_IDX).on(
                QuestionRelation.class, QuestionRelation$Table.ID_QUESTION).enable();

        new Index<Match>(Constants.MATCH_QUESTION_RELATION_IDX).on(Match.class,
                Match$Table.ID_QUESTION_RELATION).enable();

        new Index<QuestionThreshold>(Constants.QUESTION_THRESHOLDS_QUESTION_IDX).on(
                QuestionThreshold.class, QuestionThreshold$Table.ID_QUESTION).enable();

        new Index<Value>(Constants.VALUE_IDX).on(Value.class, Value$Table.ID_SURVEY).enable();
    }
}
