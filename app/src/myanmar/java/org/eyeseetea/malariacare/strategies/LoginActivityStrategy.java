package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.ProgressActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.usecase.ALoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.LoadUserAndCredentialsUseCase;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.hisp.dhis.client.sdk.ui.views.FontButton;

public class LoginActivityStrategy extends ALoginActivityStrategy {

    private static final String TAG = ".LoginActivityStrategy";

    public LoginActivityStrategy(LoginActivity loginActivity) {
        super(loginActivity);
    }

    /**
     * LoginActivity does NOT admin going backwads since it is always the first activity.
     * Thus onBackPressed closes the app
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loginActivity.startActivity(intent);
    }

    @Override
    public void onCreate() {
        if (existsLoggedUser() && PopulateDB.hasMandatoryTables()) {
            LoadUserAndCredentialsUseCase loadUserAndCredentialsUseCase =
                    new LoadUserAndCredentialsUseCase(loginActivity);

            loadUserAndCredentialsUseCase.execute();

            finishAndGo(DashboardActivity.class);
        } else {
            //TODO jsanchez, this is necessary because oncreate is called from
            //AsyncTask review Why is invoked from AsyncTask, It's not very correct
            PopulateDB.wipeDataBase();
            loginActivity.runOnUiThread(new Runnable() {
                public void run() {
                    addDemoButton();
                }
            });
        }
    }

    private boolean existsLoggedUser() {
        return User.getLoggedUser() != null;
    }

    private void addDemoButton() {
        final ViewGroup loginViewsContainer = (ViewGroup) loginActivity.findViewById(
                R.id.login_dynamic_views_container);

        loginActivity.getLayoutInflater().inflate(R.layout.demo_login_button, loginViewsContainer,
                true);

        FontButton demoButton = (FontButton) loginActivity.findViewById(R.id.demo_login_button);

        demoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Credentials demoCrededentials = Credentials.createDemoCredentials();
                loginActivity.showProgressBar();
                loginActivity.mLoginUseCase.execute(demoCrededentials,
                        new ALoginUseCase.Callback() {
                            @Override
                            public void onLoginSuccess() {
                                executePullDemo();
                            }

                            @Override
                            public void onServerURLNotValid() {
                                Log.e(this.getClass().getSimpleName(), "Server url not valid");
                            }

                            @Override
                            public void onInvalidCredentials() {
                                Log.e(this.getClass().getSimpleName(), "Invalid credentials");
                            }

                            @Override
                            public void onNetworkError() {
                                Log.e(this.getClass().getSimpleName(), "Network Error");
                            }


                            @Override
                            public void onConfigJsonInvalid() {
                                Log.d(TAG, "onConfigJsonInvalid");
                            }

                            @Override
                            public void onUnexpectedError() {
                                Log.e(this.getClass().getSimpleName(),
                                        "Config Json file not found");
                            }

                            @Override
                            public void onMaxLoginAttemptsReachedError() {
                                Log.d(TAG, "onMaxLoginAttemptsReachedError");
                            }
                        });
            }
        });
    }

    private void executePullDemo() {
        PullController pullController = new PullController(loginActivity);
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();

        PullUseCase pullUseCase = new PullUseCase(pullController, asyncExecutor, mainExecutor);

        PullFilters pullFilters = new PullFilters();
        pullFilters.setDemo(true);

        pullUseCase.execute(pullFilters, new PullUseCase.Callback() {
            @Override
            public void onComplete() {
                loginActivity.hideProgressBar();
                finishAndGo(DashboardActivity.class);
            }

            @Override
            public void onStep(PullStep step) {
                Log.d(this.getClass().getSimpleName(), step.toString());
            }

            @Override
            public void onError(String message) {
                loginActivity.hideProgressBar();
                Log.e(this.getClass().getSimpleName(), message);
            }

            @Override
            public void onPullConversionError() {
                loginActivity.hideProgressBar();
                Log.e(this.getClass().getSimpleName(), "Pull conversion error");
            }

            @Override
            public void onCancel() {
                loginActivity.hideProgressBar();
                Log.e(this.getClass().getSimpleName(), "Pull cancel");
            }

            @Override
            public void onNetworkError() {
                loginActivity.hideProgressBar();
                Log.e(this.getClass().getSimpleName(), "Network Error");
            }
        });
    }

    public void finishAndGo(Class<? extends Activity> activityClass) {
        loginActivity.startActivity(new Intent(loginActivity, activityClass));

        loginActivity.finish();
    }

    @Override
    public void finishAndGo() {
        loginActivity.onFinishLoading(null);
        finishAndGo(ProgressActivity.class);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }



    @Override
    public void initViews() {

    }

    @Override
    public void onLoginSuccess(Credentials credentials) {
        loginActivity.checkAnnouncement();
    }

    @Override
    public void initLoginUseCase(IAuthenticationManager authenticationManager) {
        loginActivity.mLoginUseCase = new LoginUseCase(authenticationManager);
    }
}
