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
import org.eyeseetea.malariacare.SplashScreenActivity;
import org.eyeseetea.malariacare.data.authentication.CredentialsReader;
import org.eyeseetea.malariacare.data.database.datasources.AuthDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.ValueLocalDataSource;
import org.eyeseetea.malariacare.data.authentication.CredentialsReader;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAuthRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IValueRepository;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.usecase.DownloadLanguageTranslationUseCase;
import org.eyeseetea.malariacare.domain.usecase.SaveSurveyFromIntentUseCase;
import org.eyeseetea.malariacare.domain.usecase.ClearAuthUseCase;
import org.eyeseetea.malariacare.network.factory.NetworkManagerFactory;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.usecase.DownloadLanguageTranslationUseCase;
import org.eyeseetea.malariacare.network.factory.NetworkManagerFactory;

public class SplashActivityStrategy extends ASplashActivityStrategy {
    public static final String INTENT_JSON_EXTRA_KEY = "ConnectVoucher";
    private Activity activity;

    public interface Callback {
        void onSuccess();
    }

    public SplashActivityStrategy(Activity mActivity) {
        super(mActivity);
        this.activity = mActivity;
        if(BuildConfig.translations) {
            PreferencesState.getInstance().loadsLanguageInActivity();
        }
    }

    public void init(final SplashScreenActivity.Callback callback) {
        String connectVoucherJson = activity.getIntent().getStringExtra(
                INTENT_JSON_EXTRA_KEY);
        clearIntentExtras();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        IValueRepository valueRepository = new ValueLocalDataSource();
        IAuthRepository authRepository = new AuthDataSource(activity.getBaseContext());
        SaveSurveyFromIntentUseCase saveSurveyFromIntentUseCase = new SaveSurveyFromIntentUseCase(asyncExecutor, mainExecutor,
                surveyRepository, valueRepository, authRepository, connectVoucherJson);
        saveSurveyFromIntentUseCase.execute(new SaveSurveyFromIntentUseCase.Callback() {
            @Override
            public void onSurveySaved(Survey survey) {
                Log.d(TAG, "Survey from other app saved");
                callback.onSuccess(canEnterApp());
            }

            @Override
            public void onEmptySurvey() {
                Log.d(TAG, "Survey from other app empty or not exist");
                clearAuth(callback);
            }

            @Override
            public void onInvalidIntentJson() {
                Log.d(TAG, "Invalid json from other app");
                clearAuth(callback);
                Toast.makeText(activity, R.string.format_error, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onInvalidProgramOrUser() {
                Log.d(TAG, "Invalid program or user");
                callback.onSuccess(canEnterApp());
            }
        });
    }

    private void clearAuth(final SplashScreenActivity.Callback callback) {
        ClearAuthUseCase clearAuthUseCase = new ClearAuthUseCase(new UIThreadExecutor(), new AsyncExecutor(), new AuthDataSource(activity.getBaseContext()));
        clearAuthUseCase.execute(new ClearAuthUseCase.Callback() {
            @Override
            public void onSuccess() {
                callback.onSuccess(canEnterApp());
            }
        });
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
    public void downloadLanguagesFromServer() {
        try {
            if (BuildConfig.downloadLanguagesFromServer) {
                Log.i(TAG, "Starting to download Languages From Server");
                CredentialsReader credentialsReader = CredentialsReader.getInstance();
                IConnectivityManager connectivity = NetworkManagerFactory.getConnectivityManager(
                        activity);
                DownloadLanguageTranslationUseCase downloader =
                        new DownloadLanguageTranslationUseCase(credentialsReader, connectivity);

                downloader.download();
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to download Languages From Server" + e.getMessage());
            e.printStackTrace();
            showToast(R.string.error_downloading_languages, e);
        }
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


    private void clearIntentExtras() {
        if(activity.getIntent().getExtras()!=null) {
            activity.getIntent().removeExtra(INTENT_JSON_EXTRA_KEY);
        }
    }
}
