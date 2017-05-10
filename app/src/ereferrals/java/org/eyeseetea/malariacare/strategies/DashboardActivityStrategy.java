package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;
import org.eyeseetea.malariacare.fragments.WebViewFragment;


public class DashboardActivityStrategy extends ADashboardActivityStrategy {

    private DashboardUnsentFragment mDashboardUnsentFragment;
    private WebViewFragment openFragment, closeFragment, statusFragment;

    public DashboardActivityStrategy(DashboardActivity dashboardActivity) {
        super(dashboardActivity);
    }


    @Override
    public void reloadStockFragment(Activity activity) {
        mDashboardUnsentFragment.reloadData();
    }

    @Override
    public boolean showStockFragment(Activity activity, boolean isMoveToLeft) {
        mDashboardUnsentFragment = new DashboardUnsentFragment();
        mDashboardUnsentFragment.setArguments(activity.getIntent().getExtras());
        mDashboardUnsentFragment.reloadData();
        mDashboardUnsentFragment.reloadHeader(activity);

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
        return isMoveToLeft;
    }

    @Override
    public void newSurvey(Activity activity) {
        Program program = new Select().from(Program.class).querySingle();
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
        openFragment = new WebViewFragment();
        Bundle bundle = mDashboardActivity.getIntent().getExtras() != null
                ? mDashboardActivity.getIntent().getExtras() : new Bundle();
        bundle.putString(WebViewFragment.WEB_VIEW_URL, "https://es.stackoverflow.com/");
        bundle.putInt(WebViewFragment.TITLE, R.string.tab_tag_assess);
        openFragment.setArguments(bundle);
        openFragment.reloadData();
        mDashboardActivity.replaceFragment(R.id.dashboard_details_container, openFragment);
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
        closeFragment = new WebViewFragment();
        Bundle bundle = mDashboardActivity.getIntent().getExtras() != null
                ? mDashboardActivity.getIntent().getExtras() : new Bundle();
        bundle.putString(WebViewFragment.WEB_VIEW_URL, "https://www.google.es/");
        bundle.putInt(WebViewFragment.TITLE, R.string.tab_tag_improve);
        closeFragment.setArguments(bundle);
        closeFragment.reloadData();
        mDashboardActivity.replaceFragment(R.id.dashboard_completed_container, closeFragment);
    }

    @Override
    public void reloadSecondFragment() {
        closeFragment.reloadData();
        closeFragment.reloadHeader(mDashboardActivity);
    }

    @Override
    public void showFourthFragment() {
        statusFragment = new WebViewFragment();
        Bundle bundle = mDashboardActivity.getIntent().getExtras() != null
                ? mDashboardActivity.getIntent().getExtras() : new Bundle();
        bundle.putString(WebViewFragment.WEB_VIEW_URL, "https://github.com/");
        bundle.putInt(WebViewFragment.TITLE, R.string.tab_tag_monitor);
        statusFragment.setArguments(bundle);
        statusFragment.reloadData();
        mDashboardActivity.replaceFragment(R.id.dashboard_charts_container, statusFragment);
    }

    @Override
    public void reloadFourthFragment() {
        statusFragment.reloadData();
        statusFragment.reloadHeader(mDashboardActivity);
    }
}
