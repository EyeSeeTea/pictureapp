package org.eyeseetea.malariacare.strategies;

import static org.eyeseetea.malariacare.utils.Utils.getUserLanguageOrDefault;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth
        .GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SettingsDataSource;
import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.io.FileDownloader;
import org.eyeseetea.malariacare.data.io.GooglePlayAppNotAvailableException;
import org.eyeseetea.malariacare.data.net.ConnectivityManager;
import org.eyeseetea.malariacare.data.remote.ElementController;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.data.repositories.ProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.IExternalVoucherRegistry;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.io.IFileDownloader;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.Settings;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.LoadingNavigationControllerException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.NoFilesException;
import org.eyeseetea.malariacare.domain.identifiers.CodeGenerator;
import org.eyeseetea.malariacare.domain.identifiers.UIDGenerator;
import org.eyeseetea.malariacare.domain.usecase.DownloadMediaUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetAppInfoUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetSettingsUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetUserUserAccountUseCase;
import org.eyeseetea.malariacare.domain.usecase.SendToExternalAppPaperVoucherUseCase;
import org.eyeseetea.malariacare.domain.usecase.TreatExternalAppResultUseCase;
import org.eyeseetea.malariacare.factories.AppInfoFactory;
import org.eyeseetea.malariacare.fragments.AVFragment;
import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;
import org.eyeseetea.malariacare.fragments.WebViewFragment;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.services.PushService;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.services.strategies.PushServiceStrategy;
import org.eyeseetea.malariacare.utils.Constants;

import java.io.File;
import java.util.Date;
import java.util.List;

public class DashboardActivityStrategy extends ADashboardActivityStrategy {

    final private String TAG = "DashboardActivityS";

    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 102;

    static final int REQUEST_AUTHORIZATION = 101;

    private DashboardUnsentFragment mDashboardUnsentFragment;
    private WebViewFragment openFragment, closeFragment, statusFragment;
    private GetSettingsUseCase mSettingUseCase;
    private DownloadMediaUseCase mDownloadMediaUseCase;
    private Credentials credentials;
    public AVFragment avFragment;
    private ImageView mRefreshButton;
    private static final long SECONDS = 1000;
    private static AlarmManager alarmManagerInstance;
    private static final int ENABLE_MANUAL_PUSH_REQUEST_CODE = 1;

    public DashboardActivityStrategy(DashboardActivity dashboardActivity) {
        super(dashboardActivity);
    }

    @Override
    public void onCreate() {
        ICredentialsRepository iCredentialsRepository = new CredentialsLocalDataSource();
        credentials = iCredentialsRepository.getLastValidCredentials();

        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IConnectivityManager mConnectivity = new ConnectivityManager(mDashboardActivity);
        IProgramRepository programRepository = new ProgramRepository();
        String path =
                PreferencesState.getInstance().getContext().getFilesDir().getAbsolutePath() + "/"
                        + Constants.MEDIA_FOLDER;
        final MediaRepository mediaRepository = new MediaRepository();
        IFileDownloader fileDownloader = new FileDownloader(
                new File(path),
                mDashboardActivity.getApplicationContext().getResources().openRawResource(
                        R.raw.driveserviceprivatekey));
        ISettingsRepository settingsRepository = new SettingsDataSource(mDashboardActivity);
        mDownloadMediaUseCase = new DownloadMediaUseCase(asyncExecutor, mainExecutor,
                fileDownloader,
                mConnectivity, programRepository, mediaRepository, settingsRepository);

        mSettingUseCase = new GetSettingsUseCase(mainExecutor, asyncExecutor, settingsRepository);


        View actionBarLayout = mDashboardActivity.getSupportActionBar().getCustomView();
        mRefreshButton = actionBarLayout.findViewById(R.id.refresh_push);
        enableDisableRefreshButton();
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPush();
            }
        });

    }

    private void enableDisableRefreshButton() {
        mRefreshButton.setEnabled(false);
        GetAppInfoUseCase getAppInfoUseCase =
                new AppInfoFactory().getGetAppInfoUseCase(mDashboardActivity);
        getAppInfoUseCase.execute(new GetAppInfoUseCase.Callback() {
            @Override
            public void onAppInfoLoaded(AppInfo appInfo) {
                boolean canMakeManualPush = appInfo.canMakeManualPush();
                mRefreshButton.setEnabled(canMakeManualPush);
                if (!canMakeManualPush) {
                    waitToEnableRefreshButton(mDashboardActivity);
                }
            }
        });
    }

    private void launchPush() {
        Intent pushIntent = new Intent(mDashboardActivity, PushService.class);
        pushIntent.putExtra(SurveyService.SERVICE_METHOD, PushService.PENDING_SURVEYS_ACTION);
        mDashboardActivity.startService(pushIntent);
    }

    private void showToast(@StringRes int text) {
        Toast.makeText(mDashboardActivity.getApplicationContext(),
                translate(text),
                Toast.LENGTH_LONG).show();
    }

    private void showToast(String message) {
        Toast.makeText(mDashboardActivity.getApplicationContext(), message,
                Toast.LENGTH_LONG).show();
    }

    private void showDialog(int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                DashboardActivity.dashboardActivity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @Override
    public void reloadStockFragment(Activity activity) {
        if (closeFragment.isAdded()) {
            closeFragment.reloadData();
            closeFragment.hideHeader();
        } else {
            showStockFragment(activity, false);
        }
    }

    @Override
    public boolean showStockFragment(Activity activity, boolean isMoveToLeft) {
        closeFragment = new WebViewFragment();
        mSettingUseCase.execute(new GetSettingsUseCase.Callback() {
            @Override
            public void onSuccess(Settings setting) {
                String webViewFragmentUrl = getWebviewUrl(R.string.url_closed_fragment);
                String url = getFormattedUrl(setting.getWebUrl(), webViewFragmentUrl);
                loadFragment(url, closeFragment, R.id.dashboard_stock_container, R.string.tab_tag_improve);
            }
        });
        return isMoveToLeft;
    }

    @Override
    public void newSurvey(final Activity activity) {

        IUserRepository userReposit = new UserAccountDataSource();
        GetUserUserAccountUseCase getUserUserAccountUseCase =
                new GetUserUserAccountUseCase(userReposit);
        getUserUserAccountUseCase.execute(new GetUserUserAccountUseCase.Callback() {
            @Override
            public void onGetUserAccount(UserAccount userAccount) {
                if (userAccount.canAddSurveys()) {
                    openNewSurvey(activity);
                } else {
                    showToast(translate(R.string.new_survey_disable));
                }
            }
        });

    }

    private void openNewSurvey(Activity activity) {
        ProgramDB programDB = ProgramDB.findById(PreferencesEReferral.getUserProgramId());
        // Put new survey in session
        SurveyDB survey = new SurveyDB(null, programDB, Session.getUserDB());
        survey.save();
        Session.setMalariaSurveyDB(survey);
        //Look for coordinates
        prepareLocationListener(activity, survey);
        mDashboardActivity.initSurvey();
    }


    @Override
    public void sendSurvey() {
        SurveyDB malariaSurvey = Session.getMalariaSurveyDB();
        malariaSurvey.updateSurveyStatus();
        if (malariaSurvey.isCompleted() && malariaSurvey.getEventUid() == null) {
            UIDGenerator uidGenerator = new UIDGenerator();
            malariaSurvey.setEventUid(CodeGenerator.generateCode());
            malariaSurvey.setVoucherUid(String.valueOf(uidGenerator.generateUID()));
            malariaSurvey.setEventDate(new Date(uidGenerator.getTimeGeneratedUID()));
            malariaSurvey.save();
            showEndSurveyMessage(malariaSurvey);
        }
    }

    @Override
    public boolean beforeExit(boolean isBackPressed) {
        SurveyDB malariaSurvey = Session.getMalariaSurveyDB();
        if (malariaSurvey != null) {
            boolean isMalariaInProgress = malariaSurvey.isInProgress();
            malariaSurvey.getValuesFromDB();
            //Exit + InProgress -> delete
            if (isBackPressed && isMalariaInProgress) {
                if (isMalariaInProgress) {
                    Session.setMalariaSurveyDB(null);
                    malariaSurvey.delete();
                }
                isBackPressed = false;
            }
        }
        return isBackPressed;
    }

    public static void onLogoutSuccess() {
        DashboardActivity.dashboardActivity.finishAndGo(LoginActivity.class);
    }

    @Override
    public void completeSurvey() {
        Session.getMalariaSurveyDB().updateSurveyStatus();
    }

    @Override
    public boolean isHistoricNewReceiptBalanceFragment(Activity activity) {
        return false;
    }

    @Override
    public void showFirstFragment() {
        mDashboardUnsentFragment = new DashboardUnsentFragment();
        mDashboardUnsentFragment.setArguments(mDashboardActivity.getIntent().getExtras());
        mDashboardUnsentFragment.reloadData();
        mDashboardUnsentFragment.reloadHeader(mDashboardActivity);

        FragmentTransaction ft = mDashboardActivity.getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.anim_slide_in_left, R.animator.anim_slide_out_left);

        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.dashboard_details_container, mDashboardUnsentFragment);

        ft.commit();
        if (BuildConfig.translations) {
            PreferencesState.getInstance().loadsLanguageInActivity();
        }

    }

    @Override
    public void reloadFirstFragment() {
        if (mDashboardUnsentFragment.isAdded()) {
            mDashboardUnsentFragment.reloadData();
        } else {
            showFirstFragment();
        }
    }

    @Override
    public void reloadFirstFragmentHeader() {
        if(!DashboardActivity.dashboardActivity.isSurveyFragmentActive()) {
            mDashboardUnsentFragment.reloadHeader(mDashboardActivity);
        }
    }

    @Override
    public void showSecondFragment() {
        openFragment = new WebViewFragment();
        mSettingUseCase.execute(new GetSettingsUseCase.Callback() {
            @Override
            public void onSuccess(Settings setting) {
                String webViewFragmentUrl = getWebviewUrl(R.string.url_open_fragment);
                String url = getFormattedUrl(setting.getWebUrl(), webViewFragmentUrl);
                loadFragment(url, openFragment, R.id.dashboard_completed_container, R.string.tab_tag_assess);
            }
        });
    }

    @Override
    public void reloadSecondFragment() {
        if (openFragment.isAdded()) {
            openFragment.reloadData();
            openFragment.hideHeader();
        } else {
            showSecondFragment();
        }
    }

    @Override
    public void showAVFragment() {
        statusFragment = new WebViewFragment();
        mSettingUseCase.execute(new GetSettingsUseCase.Callback() {
            @Override
            public void onSuccess(Settings setting) {
                String webViewFragmentUrl = getWebviewUrl(R.string.url_status_fragment);
                String url = getFormattedUrl(setting.getWebUrl(), webViewFragmentUrl);
                loadFragment(url, statusFragment, R.id.dashboard_av_container, R.string.tab_tag_monitor);
            }
        });
    }

    private void loadFragment(String url, WebViewFragment fragment, int container, int tabTag) {
        Bundle bundle = mDashboardActivity.getIntent().getExtras() != null
                ? mDashboardActivity.getIntent().getExtras() : new Bundle();
        bundle.putString(WebViewFragment.WEB_VIEW_URL, url);
        bundle.putInt(WebViewFragment.TITLE, tabTag);
        fragment.setArguments(bundle);
        fragment.reloadData();
        mDashboardActivity.replaceFragment(container,
                fragment);
        fragment.hideHeader();
    }

    private String getFormattedUrl(String settingsWebUrl, String webViewUrl) {
        return String.format(settingsWebUrl + mDashboardActivity.getString(
                R.string.composed_web_view_url), webViewUrl, credentials.getUsername(),
                credentials.getPassword(), getUserLanguageOrDefault(mDashboardActivity));
    }

    private String getWebviewUrl(int valueId) {
        if(credentials!=null && credentials.isDemoCredentials()){
            return null;
        } else{
            return mDashboardActivity.getString(valueId);
        }
    }

    @Override
    public void showFourthFragment() {
        if (avFragment == null) {
            avFragment = new AVFragment();
        }
        mDashboardActivity.replaceFragment(R.id.dashboard_charts_container, avFragment);
    }

    @Override
    public void reloadFourthFragment() {
        if (avFragment == null) {
            avFragment = new AVFragment();
        }
        mDashboardActivity.replaceFragment(R.id.dashboard_charts_container, avFragment);
        avFragment.hideHeader();
    }

    @Override
    public int getSurveyContainer() {
        return R.id.dashboard_details_container;
    }

    @Override
    public void showUnsentFragment() {
        mDashboardActivity.replaceFragment(R.id.dashboard_details_container,
                mDashboardUnsentFragment);
        mDashboardUnsentFragment.reloadHeader(mDashboardActivity);
    }

    @Override
    public void initNavigationController() throws LoadingNavigationControllerException {
        NavigationBuilder.getInstance().buildController(
                TabDB.getFirstTabWithProgram(PreferencesEReferral.getUserProgramId()));
    }

    @Override
    public void openSentSurvey() {
        mDashboardActivity.initSurvey();
    }

    @Override
    public void onResume() {
        downloadMedia();
        LocalBroadcastManager.getInstance(mDashboardActivity).registerReceiver(pushReceiver,
                new IntentFilter(PushService.class.getName()));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mDashboardActivity).unregisterReceiver(pushReceiver);
    }

    private void downloadMedia() {
        mDownloadMediaUseCase.execute(new DownloadMediaUseCase.Callback() {
            @Override
            public void onError(Throwable ex) {
                //Need to complete credentials (ack from user first time)
                if (ex.getCause() instanceof UserRecoverableAuthIOException) {
                    showToast(ex.getCause().getMessage());
                    DashboardActivity.dashboardActivity.startActivityForResult(
                            ((UserRecoverableAuthIOException) ex.getCause()).getIntent(),
                            REQUEST_AUTHORIZATION);
                    return;
                }
                //Real connection google error
                else if (ex.getCause() instanceof GooglePlayServicesAvailabilityIOException) {
                    showDialog(
                            ((GooglePlayServicesAvailabilityIOException) ex.getCause())
                                    .getConnectionStatusCode());
                    return;
                } else if (ex.getCause() instanceof GooglePlayAppNotAvailableException) {
                    /**
                     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
                     * Play Services installation via a user dialog, if possible.
                     */
                    GoogleApiAvailability apiAvailability =
                            GoogleApiAvailability.getInstance();
                    final int connectionStatusCode =
                            apiAvailability.isGooglePlayServicesAvailable(
                                    PreferencesState.getInstance().getContext());
                    if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
                        showDialog(connectionStatusCode);
                    }
                } else if (ex instanceof NetworkException) {
                    avFragment.showError(R.string.error_files_download_no_wifi, true);
                } else if (ex instanceof NoFilesException) {
                    avFragment.showError(R.string.error_files_download_no_files, true);
                } else {
                    Log.e(this.getClass().getSimpleName(), ex.getMessage());
                }
                avFragment.showProgress(false);
            }

            @Override
            public void onSuccess(int syncedFiles) {
                //the fragment should be updated to represent the removed data
                avFragment.reloadData();
                if (syncedFiles > 0) {
                    showToast(String.format("%d files synced", syncedFiles));
                }
                avFragment.showProgress(false);
                avFragment.showError(-1, false);
            }

            @Override
            public void onDownloadInProgressChanged(boolean value) {
                avFragment.showProgress(value);
            }
        });
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode == Activity.RESULT_OK) {
                    downloadMedia();
                } else {
                    Toast.makeText(mDashboardActivity.getApplicationContext(),
                            translate(
                                    R.string.google_play_required),
                            Toast.LENGTH_LONG);
                }
                break;
        }

        externalVoucherSenderResultTreatment(requestCode, resultCode, data);
    }

    private void externalVoucherSenderResultTreatment(int requestCode, int resultCode, Intent data) {
        IExternalVoucherRegistry elementController = new ElementController(DashboardActivity.dashboardActivity);
        TreatExternalAppResultUseCase treatExternalAppResultUseCase = new TreatExternalAppResultUseCase(elementController, new IExternalVoucherRegistry.Callback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "User created");
            }

            @Override
            public void onError() {
                Log.d(TAG, "User is not created");
            }
        });
        treatExternalAppResultUseCase.execute(requestCode, resultCode, data);
    }

    @Override
    public void reloadAVFragment() {
        if (statusFragment.isAdded()) {
            statusFragment.reloadData();
            statusFragment.hideHeader();
        } else {
            showAVFragment();
        }
    }

    public void showEndSurveyMessage(SurveyDB surveyDB) {
        if (surveyDB != null && !noIssueVoucher(surveyDB) && !hasPhone(surveyDB)) {
            final String voucherUId = surveyDB.getVoucherUid();

            GetSettingsUseCase getSettingsUseCase = new GetSettingsUseCase(new UIThreadExecutor(), new AsyncExecutor(),
                    new SettingsDataSource(mDashboardActivity.getBaseContext()));
            getSettingsUseCase.execute(new GetSettingsUseCase.Callback() {
                @Override
                public void onSuccess(Settings setting) {
                    DialogInterface.OnClickListener onClickListener = null;
                    if(setting.isElementActive()){
                        onClickListener = createOnClickListenerToSendVoucherToExternalApp(voucherUId, mDashboardActivity);
                    }

                    mDashboardActivity.showException(mDashboardActivity, "", String.format(
                            translate(R.string.give_voucher),
                            voucherUId), onClickListener);
                }
            });
        }
    }

    @NonNull
    private DialogInterface.OnClickListener createOnClickListenerToSendVoucherToExternalApp(final String voucherUId, final Context context) {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                IExternalVoucherRegistry elementController = new ElementController(context);
                AsyncExecutor mAsyncExecutor = new AsyncExecutor();
                UIThreadExecutor mMainExecutor = new UIThreadExecutor();
                SendToExternalAppPaperVoucherUseCase elementSentVoucherUseCase = new SendToExternalAppPaperVoucherUseCase(mMainExecutor, mAsyncExecutor,
                        elementController, new IExternalVoucherRegistry.SenderCallback() {
                    @Override
                    public void onNotInstalledApp() {
                        Toast.makeText(context,
                                translate(R.string.element_not_installed), Toast.LENGTH_LONG).show();
                    }
                });
                elementSentVoucherUseCase.execute(voucherUId);
            }
        };
    }

    private boolean noIssueVoucher(SurveyDB survey) {
        OptionDB noIssueOption = survey.getOptionSelectedForQuestionCode(
                translate(R.string.issue_voucher_qc));
        if (noIssueOption == null) {
            return false;
        }
        return noIssueOption.getName().equals(
                translate(R.string.no_voucher_on));
    }

    private boolean hasPhone(SurveyDB survey) {
        Context context = PreferencesState.getInstance().getContext();
        return !(survey.getOptionSelectedForQuestionCode(
                context.getString(R.string.phone_ownership_qc)).getName().equals(
                context.getString(R.string.no_phone_on)));
    }

    public boolean onWebViewBackPressed(TabHost tabHost) {
        View view = tabHost.getCurrentTabView();
        if (openFragment != null && mDashboardActivity.isFragmentActive(openFragment,
                R.id.dashboard_completed_container) && tabHost.getCurrentTab() == 1) {
            if (openFragment.onBackPressed()) {
                return true;
            }
        }
        if (closeFragment != null && mDashboardActivity.isFragmentActive(closeFragment,
                R.id.dashboard_stock_container) && tabHost.getCurrentTab() == 2) {
            if (closeFragment.onBackPressed()) {
                return true;
            }
        }
        if (statusFragment != null && mDashboardActivity.isFragmentActive(statusFragment,
                R.id.dashboard_av_container) && tabHost.getCurrentTab() == 3) {
            if (statusFragment.onBackPressed()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void exitReview(boolean fromSurveyList) {
        if (!DynamicTabAdapter.isClicked) {
            DynamicTabAdapter.isClicked = true;
            sendSurvey();
            mDashboardActivity.closeSurveyFragment();
            DynamicTabAdapter.isClicked = false;
        }
    }

    private BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            managePushIntent(intent);
            mDashboardUnsentFragment.reloadData();
        }
    };

    private void managePushIntent(Intent intent) {
        if (intent.getBooleanExtra(PushServiceStrategy.PUSH_IS_START, false)) {
            mRefreshButton.setEnabled(false);
            mRefreshButton.startAnimation(
                    AnimationUtils.loadAnimation(mRefreshButton.getContext(),
                            R.anim.rotate_center));
            waitToEnableRefreshButton(mDashboardActivity);
        } else {
            mRefreshButton.clearAnimation();
        }

        if (intent.getBooleanExtra(PushServiceStrategy.PUSH_NETWORK_ERROR, false)) {
            showError(R.string.push_network_error);
        }
    }

    public void onConnectivityStatusChange() {
        downloadMedia();
    }

    public void initStockControlFragment() {

    }

    public void openPendingSurveyIfRequired() {
        if (Session.hasSurveyToComplete()) {
            openUncompletedSurvey();
            Session.setHasSurveyToComplete(false);
        }
    }

    private void openUncompletedSurvey() {
        SurveyDB survey;
        List<SurveyDB> uncompletedSurveys = SurveyDB.getAllUncompletedSurveys();
        if (!uncompletedSurveys.isEmpty()) {
            survey = uncompletedSurveys.get(uncompletedSurveys.size() - 1);
            Session.setMalariaSurveyDB(survey);
            //Look for coordinates
            prepareLocationListener(mDashboardActivity, survey);
            mDashboardActivity.initSurvey();
        }
    }


    private void waitToEnableRefreshButton(Context context) {
        long pushPeriod = Long.parseLong(context.getString(R.string.ENABLE_MANUAL_PUSH_PERIOD));
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                enableDisableRefreshButton();
            }
        };
        new Handler().postDelayed(runnable, pushPeriod * SECONDS);
    }

    public void showError(int message) {
        Toast.makeText(mDashboardActivity, translate(message),
                Toast.LENGTH_LONG).show();
    }

    private String translate(@StringRes int resourceId) {
        return mDashboardActivity.translate(resourceId);
    }
}
