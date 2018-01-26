package org.eyeseetea.malariacare.strategies;

import static org.eyeseetea.malariacare.services.strategies.APushServiceStrategy.TAG;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.usecase.GetLastInsertedCredentialsUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

public class SplashActivityStrategy extends ASplashActivityStrategy {
    public SplashActivityStrategy(Activity mActivity) {
        super(mActivity);
        if(BuildConfig.translations) {
            PreferencesState.getInstance().loadsLanguageInActivity();
        }
    }

    @Override
    public void finishAndGo() {
        super.finishAndGo(LoginActivity.class);
    }

    @Override
    public boolean canEnterApp() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return true;
        }else {
            showDialogNotSupportedAndroid();
            return false;
        }
    }

    private void showDialogNotSupportedAndroid() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(R.string.dialog_title_error)
                .setMessage(R.string.error_android_version_not_supported)
                .setPositiveButton(R.string.provider_redeemEntry_msg_matchingOk,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        activity.finish();
                    }
                })
                .setCancelable(false);
        builder.show();
    }

    @Override
    public void downloadLanguagesFromServer() throws Exception {
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        ICredentialsRepository credentialsRepository = new CredentialsLocalDataSource();
        GetLastInsertedCredentialsUseCase getLastInsertedCredentialsUseCase =
                new GetLastInsertedCredentialsUseCase(mainExecutor, asyncExecutor,
                        credentialsRepository);
        getLastInsertedCredentialsUseCase.execute(
                new GetLastInsertedCredentialsUseCase.Callback() {
                    @Override
                    public void onGetUsername(Credentials credentials) {
                        if (credentials == null || !credentials.isDemoCredentials()) {
                            try {
                                SplashActivityStrategy.super.downloadLanguagesFromServer();
                            } catch (Exception e) {
                                Log.e(TAG, "Unable to download Languages From Server"
                                        + e.getMessage());
                                e.printStackTrace();
                                showToast(R.string.error_downloading_languages, e);
                            }
                        }
                    }
                });
    }

    private void showToast(int titleResource, final Exception e) {
        final String title = activity.getResources().getString(titleResource);
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, title + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
