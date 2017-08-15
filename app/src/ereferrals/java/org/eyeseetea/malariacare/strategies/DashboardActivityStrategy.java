package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.entity.UIDGenerator;
import org.eyeseetea.malariacare.domain.usecase.GetUrlForWebViewsUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.DownloadMediaUseCase;
import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;
import org.eyeseetea.malariacare.fragments.WebViewFragment;
import org.eyeseetea.malariacare.domain.exception.LoadingNavigationControllerException;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;


public class DashboardActivityStrategy extends ADashboardActivityStrategy {

    private DashboardUnsentFragment mDashboardUnsentFragment;
    private WebViewFragment openFragment, closeFragment, statusFragment;
    private GetUrlForWebViewsUseCase mGetUrlForWebViewsUseCase;
    private DownloadMediaUseCase mDownloadMediaUseCase;

    public DashboardActivityStrategy(DashboardActivity dashboardActivity) {
        super(dashboardActivity);
    }

    @Override
    public void onCreate() {
        ICredentialsRepository iCredentialsRepository = new CredentialsLocalDataSource();
        mGetUrlForWebViewsUseCase = new GetUrlForWebViewsUseCase(mDashboardActivity,
                iCredentialsRepository);

        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        MediaRepository mediaRepository = new MediaRepository();
        mDownloadMediaUseCase = new DownloadMediaUseCase(asyncExecutor, mediaRepository, mDashboardActivity.getApplicationContext());
    }

    @Override
    public void reloadStockFragment(Activity activity) {
        mDashboardUnsentFragment.reloadData();
        mDashboardUnsentFragment.reloadHeader(activity, R.string.tab_tag_stock);
    }

    @Override
    public boolean showStockFragment(Activity activity, boolean isMoveToLeft) {
        mDashboardUnsentFragment = new DashboardUnsentFragment();
        mDashboardUnsentFragment.setArguments(activity.getIntent().getExtras());
        mDashboardUnsentFragment.reloadData();

        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        if (isMoveToLeft) {
            isMoveToLeft = false;
            ft.setCustomAnimations(R.animator.anim_slide_in_right, R.animator.anim_slide_out_right);
        } else {
            ft.setCustomAnimations(R.animator.anim_slide_in_left, R.animator.anim_slide_out_left);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.dashboard_stock_container, mDashboardUnsentFragment);

        ft.commit();
        if(BuildConfig.translations) {
            PreferencesState.getInstance().loadsLanguageInActivity();
        }
        return isMoveToLeft;
    }

    @Override
    public void newSurvey(Activity activity) {
        ProgramDB programDB = ProgramDB.findById(PreferencesEReferral.getUserProgramId());
        // Put new survey in session
        SurveyDB survey = new SurveyDB(null, programDB, Session.getUserDB());
        survey.save();
        Session.setMalariaSurveyDB(survey);
        //Look for coordinates
        prepareLocationListener(activity, survey);
    }

    @Override
    public void sendSurvey() {
        SurveyDB malariaSurvey = Session.getMalariaSurveyDB();
        malariaSurvey.updateSurveyStatus();
        if (malariaSurvey.isCompleted()) {
            malariaSurvey.setEventUid(String.valueOf(UIDGenerator.generateUID()));
            malariaSurvey.save();
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
                        openFragment.reloadHeader(mDashboardActivity, R.string.tab_tag_assess);
                        mDashboardActivity.replaceFragment(R.id.dashboard_details_container,
                                openFragment);
                    }
                });
    }

    @Override
    public void reloadFirstFragment() {
        openFragment.reloadData();
    }

    @Override
    public void reloadFirstFragmentHeader() {
        openFragment.reloadHeader(mDashboardActivity);
    }

    @Override
    public void showSecondFragment() {
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
                        mDashboardActivity.replaceFragment(R.id.dashboard_completed_container,
                                closeFragment);
                    }
                });
    }

    @Override
    public void reloadSecondFragment() {
        closeFragment.reloadData();
        closeFragment.reloadHeader(mDashboardActivity);
    }

    @Override
    public void showFourthFragment() {
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
                        mDashboardActivity.replaceFragment(R.id.dashboard_charts_container,
                                statusFragment);
                    }
                });
    }

    @Override
    public void reloadFourthFragment() {
        statusFragment.reloadData();
        statusFragment.reloadHeader(mDashboardActivity);
    }

    @Override
    public int getSurveyContainer() {
        return R.id.dashboard_stock_container;
    }

    @Override
    public void showUnsentFragment() {
        mDashboardActivity.replaceFragment(R.id.dashboard_stock_container,
                mDashboardUnsentFragment);
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


    public void onResume() {
        mDownloadMediaUseCase.execute(mDashboardActivity.getApplicationContext());
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
            case DownloadMediaUseCase.REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != Activity.RESULT_OK) {
                    Toast.makeText(mDashboardActivity.getApplicationContext(), mDashboardActivity.getApplicationContext().getString(R.string.google_play_required),
                            Toast.LENGTH_LONG);
                } else {
                    mDownloadMediaUseCase.execute(mDashboardActivity.getApplicationContext());
                }
                break;
        }
    }
}
