package org.eyeseetea.malariacare.strategies;

import static org.eyeseetea.malariacare.R.id.progress_message;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.EyeSeeTeaApplication;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.domain.AutoconfigureException;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.LoadingNavigationControllerException;
import org.eyeseetea.malariacare.domain.exception.WarningException;
import org.eyeseetea.malariacare.domain.exception.organisationunit
        .ExistsMoreThanOneOrgUnitByPhoneException;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;
import org.eyeseetea.malariacare.utils.Permissions;

public class SplashActivityStrategy extends ASplashActivityStrategy {
    private PullUseCase mPullUseCase;
    private PullFilters mPullFilters;
    private Activity mActivity;
    private boolean hasAutoconfigureError;
    public SplashActivityStrategy(Activity mActivity) {
        super(mActivity);
        this.mActivity = mActivity;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (EyeSeeTeaApplication.permissions == null) {
                EyeSeeTeaApplication.permissions = Permissions.getInstance(mActivity);
            }

            if (EyeSeeTeaApplication.permissions.getPermission(Permissions.PHONE_STATE_REQUEST_CODE)
                    != null) {
                Permissions.Permission permission = EyeSeeTeaApplication.permissions.getPermission(
                        Permissions.FINE_LOCATION_REQUEST_CODE);

                EyeSeeTeaApplication.permissions.requestPermission(permission.getDefinition(),
                        permission.getCode());
            }
        }

    }

    @Override
    public void finishAndGo() {

    }

    @Override
    public void initPullFilters(final PullFilters pullFilters) {
        pullFilters.setAutoConfig(true);
        pullFilters.setPullMetaData(true);
    }

    @Override
    public void executePull(PullUseCase pullUseCase, final PullFilters pullFilters) {
        mPullUseCase = pullUseCase;
        mPullFilters = pullFilters;
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || EyeSeeTeaApplication.permissions.getPermission(
                Permissions.PHONE_STATE_REQUEST_CODE)
                == null) {
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
                    if (step == PullStep.AUTO_CONFIGURE_ORG_UNIT) {
                        ((TextView) activity.findViewById(progress_message)).setText(
                                R.string.auto_configuring);
                    }
                    Log.d(this.getClass().getSimpleName(), step.toString());
                }

                @Override
                public void onError(Throwable throwable) {
                    Log.e(this.getClass().getSimpleName(),
                            "error message" + throwable.getMessage());
                    if (throwable instanceof AutoconfigureException) {
                        hasAutoconfigureError = true;
                        showErrorAutoConfiguration(R.string.error_auto_configuration);
                        return;
                    }
                    if(throwable instanceof ApiCallException){
                        hasAutoconfigureError = true;
                    }
                    showErrorAutoConfiguration(R.string.error_auto_configuration_unexpected);
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
                public void onWarning(WarningException warning) {
                    if (warning instanceof ExistsMoreThanOneOrgUnitByPhoneException) {
                        ExistsMoreThanOneOrgUnitByPhoneException exception =
                                (ExistsMoreThanOneOrgUnitByPhoneException) warning;

                        TextView infoTextView = (TextView) activity.findViewById(progress_message);

                        infoTextView.setText(infoTextView.getText() + "\n" +
                                infoTextView.getContext().getString(
                                        R.string.exists_more_than_one_org_unit_by_phone,
                                        exception.getPhone()));
                    }
                }

                @Override
                public void onCancel() {
                    Log.e(this.getClass().getSimpleName(), "Pull oncancel");
                    goNextActivity();
                }
            });
        }
    }

    private void showErrorAutoConfiguration(int messageId) {
        new AlertDialog.Builder(activity)
                .setTitle(R.string.error_message)
                .setMessage(messageId)
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
            } else {
                EyeSeeTeaApplication.permissions.requestNextPermission(mActivity);
            }
        } else {
            if (requestCode == Permissions.PHONE_STATE_REQUEST_CODE) {
                showErrorAutoConfiguration(R.string.error_auto_configuration);
            } else {
                Permissions.Permission permission = EyeSeeTeaApplication.permissions.getPermission(
                        Permissions.PHONE_STATE_REQUEST_CODE);

                EyeSeeTeaApplication.permissions.requestPermission(permission.getDefinition(),
                        permission.getCode());
            }
        }
    }
}