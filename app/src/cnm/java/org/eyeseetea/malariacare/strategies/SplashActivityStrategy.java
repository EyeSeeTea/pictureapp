package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.EyeSeeTeaApplication;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.datasources.AppInfoDataSource;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.domain.AutoconfigureException;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;
import org.eyeseetea.malariacare.domain.exception.LoadingNavigationControllerException;
import org.eyeseetea.malariacare.domain.usecase.GetAppInfoUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.utils.Permissions;

public class SplashActivityStrategy extends ASplashActivityStrategy {
    private PullUseCase mPullUseCase;
    private PullFilters mPullFilters;
    private Activity mActivity;
    private boolean hasAutoconfigureError;
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
    public void initPullFilters(final PullFilters pullFilters) {
        pullFilters.setAutoConfig(true);
        final IAppInfoRepository appInfoDataSource = new AppInfoDataSource();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        GetAppInfoUseCase getAppInfoUseCase = new GetAppInfoUseCase(mainExecutor, asyncExecutor,
                appInfoDataSource);
        getAppInfoUseCase.execute(new GetAppInfoUseCase.Callback() {
            @Override
            public void onAppInfoLoaded(AppInfo appInfo) {
                pullFilters.setPullMetaData(!appInfo.isMetadataDownloaded());
            }
        });

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
                    if (!hasAutoconfigureError) {
                        goNextActivity();
                    }
                }

                @Override
                public void onStep(PullStep step) {
                    Log.d(this.getClass().getSimpleName(), step.toString());
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e(this.getClass().getSimpleName(),
                            "error message" + throwable.getMessage());
                    if (throwable instanceof AutoconfigureException) {
                        showErrorAutoConfiguration();
                        hasAutoconfigureError = true;
                    }
                }

                @Override
                public void onNetworkError() {
                    Log.e(this.getClass().getSimpleName(), "Network Error");
                    goNextActivity();
                }

                @Override
                public void onPullConversionError() {
                    Log.e(this.getClass().getSimpleName(), "Pull Conversion Error");
                    goNextActivity();
                }

                @Override
                public void onCancel() {
                    Log.e(this.getClass().getSimpleName(), "Pull oncancel");
                    goNextActivity();
                }
            });
        }
    }

    private void showErrorAutoConfiguration() {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.error_message)
                .setMessage(R.string.error_auto_configuration)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        goNextActivity();
                    }
                }).create().show();
    }

    private void goNextActivity() {
        try {
            NavigationBuilder.getInstance().buildController(TabDB.getFirstTab());
            SplashActivityStrategy.this.finishAndGo(DashboardActivity.class);
        } catch (LoadingNavigationControllerException ex) {
            Log.e(this.getClass().getSimpleName(), ex.getMessage());
        }
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
}