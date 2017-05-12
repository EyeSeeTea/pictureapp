package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.widget.EditText;

import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.ProgressActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.InvalidLoginAttemptsRepositoryLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.remote.OrganisationUnitDataSource;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.InvalidLoginAttempts;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

import java.util.Date;

public class LoginActivityStrategy extends ALoginActivityStrategy {

    private static final String TAG = ".LoginActivityStrategy";
    public static final String EXIT = "exit";

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
        if (loginActivity.getIntent().getBooleanExtra(EXIT, false)) {
            loginActivity.finish();
        }
    }

    public void finishAndGo(Class<? extends Activity> activityClass) {
        loginActivity.startActivity(new Intent(loginActivity, activityClass));

        loginActivity.finish();
    }

    @Override
    public void finishAndGo() {
        finishAndGo(ProgressActivity.class);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }


    @Override
    public void initViews() {
        EditText passwordEditText = (EditText) loginActivity.findViewById(R.id.edittext_password);
        passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        TextInputLayout passwordHint =
                (TextInputLayout) loginActivity.findViewById(R.id.password_hint);
        passwordHint.setHint(loginActivity.getResources().getText(R.string.login_pin));
    }

    @Override
    public void onLoginSuccess(final Credentials credentials) {
        loginActivity.checkAnnouncement();
    }


    @Override
    public void onBadCredentials() {
        super.onBadCredentials();
    }

    @Override
    public void disableLogin() {
        super.disableLogin();
        loginActivity.enableLogin(false);
        checkEnableLogin();
    }

    private void checkEnableLogin() {
        IInvalidLoginAttemptsRepository invalidLoginAttemptsLocalDataSource =
                new InvalidLoginAttemptsRepositoryLocalDataSource();
        final InvalidLoginAttempts invalidLoginAttempts =
                invalidLoginAttemptsLocalDataSource.getInvalidLoginAttempts();
        if (!invalidLoginAttempts.isLoginEnabled()) {
            loginActivity.enableLogin(false);
            final Handler h = new Handler();
            final int delay = 1000; //milliseconds
            h.postDelayed(new Runnable() {
                public void run() {
                    if (!loginActivity.isFinishing()) {
                        if (!invalidLoginAttempts.isLoginEnabled()) {
                            h.postDelayed(this, delay);
                        } else {
                            loginActivity.enableLogin(true);
                        }
                    }
                }
            }, delay);
        }
    }

    @Override
    public void onStart() {
        checkEnableLogin();
    }


    public boolean canEnableLoginButtonOnTextChange() {
        long timeEnabled = PreferencesEReferral.getTimeLoginEnables();
        long currentTime = new Date().getTime();
        return currentTime > timeEnabled;
    }

    @Override
    public void onTextChange() {
        if (canEnableLoginButtonOnTextChange()) {
            super.onTextChange();
        }
    }


    public void initLoginUseCase(IAuthenticationManager authenticationManager) {
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        ICredentialsRepository credentialsLocalDataSoruce = new CredentialsLocalDataSource();
        IOrganisationUnitRepository organisationDataSource = new OrganisationUnitDataSource();
        IInvalidLoginAttemptsRepository
                iInvalidLoginAttemptsRepository =
                new InvalidLoginAttemptsRepositoryLocalDataSource();
        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        loginActivity.mLoginUseCase = new LoginUseCase(authenticationManager, mainExecutor,
                asyncExecutor, organisationDataSource, credentialsLocalDataSoruce,
                iInvalidLoginAttemptsRepository, surveyRepository);
    }
}