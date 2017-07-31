package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.usecase.GetUrlForWebViewsUseCase;
import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;
import org.eyeseetea.malariacare.fragments.WebViewFragment;
import org.eyeseetea.malariacare.domain.exception.LoadingNavigationControllerException;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;
import org.eyeseetea.malariacare.fragments.MonitorFragment;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;


public class DashboardActivityStrategy extends ADashboardActivityStrategy {

    private DashboardUnsentFragment mDashboardUnsentFragment;
    private WebViewFragment openFragment, closeFragment, statusFragment;
    private GetUrlForWebViewsUseCase mGetUrlForWebViewsUseCase;

    public DashboardActivityStrategy(DashboardActivity dashboardActivity) {
        super(dashboardActivity);
    }

    @Override
    public void onCreate() {
        ICredentialsRepository iCredentialsRepository = new CredentialsLocalDataSource();
        mGetUrlForWebViewsUseCase = new GetUrlForWebViewsUseCase(mDashboardActivity,
                iCredentialsRepository);
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
        Program program = Program.findById(PreferencesEReferral.getUserProgramId());
        // Put new survey in session
        Survey survey = new Survey(null, program, Session.getUser());
        survey.save();
        Session.setMalariaSurvey(survey);
        //Look for coordinates
        prepareLocationListener(activity, survey);
    }

    @Override
    public void sendSurvey() {
        Session.getMalariaSurvey().updateSurveyStatus();
    }

    @Override
    public boolean beforeExit(boolean isBackPressed) {
        Survey malariaSurvey = Session.getMalariaSurvey();
        if (malariaSurvey != null) {
            boolean isMalariaInProgress = malariaSurvey.isInProgress();
            malariaSurvey.getValuesFromDB();
            //Exit + InProgress -> delete
            if (isBackPressed && isMalariaInProgress) {
                if (isMalariaInProgress) {
                    Session.setMalariaSurvey(null);
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
        Session.getMalariaSurvey().updateSurveyStatus();
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
                Tab.getFirstTabWithProgram(PreferencesEReferral.getUserProgramId()));
    }

    @Override
    public void openSentSurvey() {
        mDashboardActivity.initSurvey();
    }
}
