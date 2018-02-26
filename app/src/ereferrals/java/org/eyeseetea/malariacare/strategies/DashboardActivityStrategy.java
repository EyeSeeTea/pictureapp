package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import org.eyeseetea.malariacare.data.database.datasources.ConfigurationLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.LanguagesLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
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
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.io.IFileDownloader;
import org.eyeseetea.malariacare.domain.boundary.repositories.IConfigurationRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ILanguageRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.UIDGenerator;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.LoadingNavigationControllerException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.NoFilesException;
import org.eyeseetea.malariacare.domain.usecase.GetUrlForWebViewsUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetUserUserAccountUseCase;
import org.eyeseetea.malariacare.domain.usecase.VerifyLanguagesAndConfigFilesWereDownloadedUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.DownloadMediaUseCase;
import org.eyeseetea.malariacare.fragments.AVFragment;
import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;
import org.eyeseetea.malariacare.fragments.WebViewFragment;
import org.eyeseetea.malariacare.layout.adapters.survey.DynamicTabAdapter;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.services.PushService;
import org.eyeseetea.malariacare.services.strategies.PushServiceStrategy;
import org.eyeseetea.malariacare.utils.Constants;

import java.io.File;
import java.util.Date;

public class DashboardActivityStrategy extends ADashboardActivityStrategy {
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 102;

    static final int REQUEST_AUTHORIZATION = 101;

    private DashboardUnsentFragment mDashboardUnsentFragment;
    private WebViewFragment openFragment, closeFragment, statusFragment;
    private GetUrlForWebViewsUseCase mGetUrlForWebViewsUseCase;
    private DownloadMediaUseCase mDownloadMediaUseCase;
    public AVFragment avFragment;

    public DashboardActivityStrategy(DashboardActivity dashboardActivity) {
        super(dashboardActivity);
    }

    @Override
    public void onCreate() {

        IConfigurationRepository configurationRepository = new ConfigurationLocalDataSource();
        ILanguageRepository languageRepository = new LanguagesLocalDataSource();

        VerifyLanguagesAndConfigFilesWereDownloadedUseCase downloadedUseCase =
                new VerifyLanguagesAndConfigFilesWereDownloadedUseCase(
                        configurationRepository, languageRepository,
                        new VerifyLanguagesAndConfigFilesWereDownloadedUseCase.Callback() {
                    @Override
                    public void onSoftLoginStringTranslationFailed() {
                        showToast(R.string.warning_strings_download_failed);
                    }

                    @Override
                    public void onFullLoginStringTranslationOrConfigFilesFailed(
                            TypeOfFailure typeOfFailed) {
                        switch (typeOfFailed) {
                            case TRANSLATIONS:
                                showToast(R.string.error_unable_to_download_translations);
                                break;
                            case CONFIGURATION_FILES:
                                showToast(R.string.error_unable_to_download_configuration_files);
                                break;
                            case TRANSLATIONS_AND_CONFIGURATION_FILES:
                                showToast(
                                        R.string.error_unable_to_download_translations_and_configuration_files);
                                break;
                        }

                        mDashboardActivity.finishAndGo(LoginActivity.class);
                    }
                });

        downloadedUseCase.run();

        ICredentialsRepository iCredentialsRepository = new CredentialsLocalDataSource();
        mGetUrlForWebViewsUseCase = new GetUrlForWebViewsUseCase(mDashboardActivity,
                iCredentialsRepository);

        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IConnectivityManager mConnectivity = new ConnectivityManager();
        IProgramRepository programRepository = new ProgramLocalDataSource();
        String path =
                PreferencesState.getInstance().getContext().getFilesDir().getAbsolutePath() + "/"
                        + Constants.MEDIA_FOLDER;
        final MediaRepository mediaRepository = new MediaRepository();
        IFileDownloader fileDownloader = new FileDownloader(
                new File(path),
                mDashboardActivity.getApplicationContext().getResources().openRawResource(
                        R.raw.driveserviceprivatekey));
        mDownloadMediaUseCase = new DownloadMediaUseCase(asyncExecutor, mainExecutor,
                fileDownloader,
                mConnectivity, programRepository, mediaRepository);
    }

    private void showToast(@StringRes int text) {
        Toast.makeText(mDashboardActivity.getApplicationContext(), text,
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
        mGetUrlForWebViewsUseCase.execute(GetUrlForWebViewsUseCase.CLOSED_TYPE,
                new GetUrlForWebViewsUseCase.Callback() {
                    @Override
                    public void onGetUrl(String url) {
                        closeFragment = new WebViewFragment();
                        Bundle bundle = mDashboardActivity.getIntent().getExtras() != null
                                ? mDashboardActivity.getIntent().getExtras() : new Bundle();
                        bundle.putString(WebViewFragment.WEB_VIEW_URL, url);
                        bundle.putInt(WebViewFragment.TITLE, R.string.tab_tag_improve);
                        closeFragment.setArguments(bundle);
                        closeFragment.reloadData();
                        closeFragment.hideHeader();
                        mDashboardActivity.replaceFragment(R.id.dashboard_stock_container,
                                closeFragment);
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
                    showToast(activity.getResources().getString(R.string.new_survey_disable));
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
            malariaSurvey.setEventUid(String.valueOf(uidGenerator.generateUID()));
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
        mDashboardUnsentFragment.reloadHeader(mDashboardActivity);
    }

    @Override
    public void showSecondFragment() {
        mGetUrlForWebViewsUseCase.execute(GetUrlForWebViewsUseCase.OPEN_TYPE,
                new GetUrlForWebViewsUseCase.Callback() {
                    @Override
                    public void onGetUrl(String url) {
                        openFragment = new WebViewFragment();
                        Bundle bundle = mDashboardActivity.getIntent().getExtras() != null
                                ? mDashboardActivity.getIntent().getExtras() : new Bundle();
                        bundle.putString(WebViewFragment.WEB_VIEW_URL, url);
                        bundle.putInt(WebViewFragment.TITLE, R.string.tab_tag_assess);
                        openFragment.setArguments(bundle);
                        openFragment.reloadData();
                        openFragment.hideHeader();
                        mDashboardActivity.replaceFragment(R.id.dashboard_completed_container,
                                openFragment);
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
        mGetUrlForWebViewsUseCase.execute(GetUrlForWebViewsUseCase.STATUS_TYPE,
                new GetUrlForWebViewsUseCase.Callback() {
                    @Override
                    public void onGetUrl(String url) {
                        statusFragment = new WebViewFragment();
                        Bundle bundle = mDashboardActivity.getIntent().getExtras() != null
                                ? mDashboardActivity.getIntent().getExtras() : new Bundle();
                        bundle.putString(WebViewFragment.WEB_VIEW_URL, url);
                        bundle.putInt(WebViewFragment.TITLE, R.string.tab_tag_monitor);
                        statusFragment.setArguments(bundle);
                        statusFragment.reloadData();
                        mDashboardActivity.replaceFragment(R.id.dashboard_av_container,
                                statusFragment);
                        statusFragment.hideHeader();
                    }
                });
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != Activity.RESULT_OK) {
                    Toast.makeText(mDashboardActivity.getApplicationContext(),
                            mDashboardActivity.getApplicationContext().getString(
                                    R.string.google_play_required),
                            Toast.LENGTH_LONG);
                } else {
                    downloadMedia();
                }
                break;
        }
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
            mDashboardActivity.showException("", String.format(
                    mDashboardActivity.getResources().getString(R.string.give_voucher),
                    surveyDB.getEventUid()));
        }
    }

    private boolean noIssueVoucher(SurveyDB survey) {
        OptionDB noIssueOption = survey.getOptionSelectedForQuestionCode(
                mDashboardActivity.getString(R.string.issue_voucher_qc));
        if (noIssueOption == null) {
            return false;
        }
        return noIssueOption.getName().equals(
                mDashboardActivity.getString(R.string.no_voucher_on));
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
    public void exitReview() {
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
            showHideProgressPush(intent);
            mDashboardUnsentFragment.reloadData();
        }
    };

    private void showHideProgressPush(Intent intent) {
        View actionBarLayout = mDashboardActivity.getSupportActionBar().getCustomView();
        ImageView refreshPush = (ImageView) actionBarLayout.findViewById(R.id.refresh_push);
        if (intent.getBooleanExtra(PushServiceStrategy.PUSH_IS_START, false)) {
            refreshPush.setVisibility(View.VISIBLE);
            refreshPush.startAnimation(
                    AnimationUtils.loadAnimation(refreshPush.getContext(),
                            R.anim.rotate_center));
        } else {
            refreshPush.clearAnimation();
            refreshPush.setVisibility(View.GONE);
        }
    }

    public void onConnectivityStatusChange() {
        downloadMedia();
    }
}
