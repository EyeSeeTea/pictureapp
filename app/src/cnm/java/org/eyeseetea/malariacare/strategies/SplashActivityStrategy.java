package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.EyeSeeTeaApplication;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.exception.LoadingNavigationControllerException;
import org.eyeseetea.malariacare.domain.usecase.ALoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.LoginUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;
import org.eyeseetea.malariacare.utils.Permissions;

public class SplashActivityStrategy extends ASplashActivityStrategy {
    private PullUseCase mPullUseCase;
    private PullFilters mPullFilters;
    private Activity mActivity;
    public SplashActivityStrategy(Activity mActivity) {
        super(mActivity);
        this.mActivity = mActivity;
        if (EyeSeeTeaApplication.permissions == null) {
            EyeSeeTeaApplication.permissions = Permissions.getInstance(mActivity);
        }

        if (EyeSeeTeaApplication.permissions.getPermission(Permissions.PHONE_STATE_REQUEST_CODE)
                != null) {
            Permissions.Permission permission = EyeSeeTeaApplication.permissions.getPermission(
                    Permissions.PHONE_STATE_REQUEST_CODE);

            EyeSeeTeaApplication.permissions.requestPermission(permission.getDefinition(),
                    permission.getCode());
        }

    }

    @Override
    public void finishAndGo() {

    }

    @Override
    public void initPullFilters(PullFilters pullFilters) {
        pullFilters.setAutoConfig(true);
    }

    @Override
    public void executePull(PullUseCase pullUseCase, final PullFilters pullFilters) {
        mPullUseCase = pullUseCase;
        mPullFilters = pullFilters;
        if (EyeSeeTeaApplication.permissions.getPermission(Permissions.PHONE_STATE_REQUEST_CODE)
                == null || pullFilters.isDemo()) {
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
                    onPullCancelOrError(pullFilters);
                }

                @Override
                public void onNetworkError() {
                    Log.e(this.getClass().getSimpleName(), "Network Error");
                    onPullCancelOrError(pullFilters);
                }

                @Override
                public void onPullConversionError() {
                    Log.e(this.getClass().getSimpleName(), "Pull Conversion Error");
                    onPullCancelOrError(pullFilters);
                }

                @Override
                public void onCancel() {
                    Log.e(this.getClass().getSimpleName(), "Pull oncancel");
                    onPullCancelOrError(pullFilters);
                }
            });
        }
    }

    public void onPullCancelOrError(PullFilters pullFilters) {
        if (pullFilters.isAutoConfig()) {
            showErrorAutoConfiguration();
        } else {
            showErrorInitApp();
        }
    }


    private void showErrorAutoConfiguration() {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.error_message)
                .setMessage(R.string.error_auto_configuration)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        continueWithDemoUser();
                    }
                }).create().show();
    }

    private void showErrorInitApp() {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.error_message)
                .setMessage(R.string.error_initialize_app)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        mActivity.finish();
                    }
                }).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
            int[] grantResults) {
        if (Permissions.processAnswer(requestCode, permissions, grantResults)) {
            if (EyeSeeTeaApplication.permissions.getPermission(Permissions.PHONE_STATE_REQUEST_CODE)
                    == null && mPullUseCase != null && mPullFilters != null) {
                executePull(mPullUseCase, mPullFilters);
            }
        } else {
            if (requestCode == Permissions.PHONE_STATE_REQUEST_CODE) {
                showErrorAutoConfiguration();
            } else if (EyeSeeTeaApplication.permissions.getPermission(
                    Permissions.PHONE_STATE_REQUEST_CODE)
                    != null) {
                EyeSeeTeaApplication.permissions.requestNextPermission();
            }
        }
    }

    private void continueWithDemoUser() {
        Credentials demoCredentials = Credentials.createDemoCredentials();
        AuthenticationManager authenticationManager = new AuthenticationManager(mActivity);
        LoginUseCase loginUseCase = new LoginUseCase(authenticationManager);
        loginUseCase.execute(demoCredentials, new ALoginUseCase.Callback() {
            @Override
            public void onLoginSuccess() {
                PullFilters pullFilters = new PullFilters();
                pullFilters.setDemo(true);
                if (mPullUseCase != null) {
                    executePull(mPullUseCase, pullFilters);
                } else {
                    showErrorInitApp();
                }
            }

            @Override
            public void onServerURLNotValid() {
                showErrorInitApp();
            }

            @Override
            public void onInvalidCredentials() {
                showErrorInitApp();
            }

            @Override
            public void onServerPinChanged() {
                showErrorInitApp();
            }

            @Override
            public void onNetworkError() {
                showErrorInitApp();
            }

            @Override
            public void onConfigJsonInvalid() {
                showErrorInitApp();
            }

            @Override
            public void onUnexpectedError() {
                showErrorInitApp();
            }

            @Override
            public void onMaxLoginAttemptsReachedError() {
                showErrorInitApp();
            }
        });


    }
}