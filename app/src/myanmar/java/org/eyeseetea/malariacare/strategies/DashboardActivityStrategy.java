package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.usecase.CompletionSurveyUseCase;
import org.eyeseetea.malariacare.fragments.HistoricReceiptBalanceFragment;
import org.eyeseetea.malariacare.fragments.NewReceiptBalanceFragment;
import org.eyeseetea.malariacare.fragments.StockFragment;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.Date;


public class DashboardActivityStrategy extends ADashboardActivityStrategy {
    private StockFragment stockFragment;

    public DashboardActivityStrategy(DashboardActivity dashboardActivity) {
        super(dashboardActivity);
    }

    @Override
    public void reloadStockFragment(Activity activity) {
        stockFragment.reloadData();
        stockFragment.reloadHeader(activity);
    }

    @Override
    public boolean showStockFragment(Activity activity, boolean isMoveToLeft) {
        stockFragment = new StockFragment();
        stockFragment.setArguments(activity.getIntent().getExtras());
        stockFragment.reloadData();
        stockFragment.reloadHeader(activity);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        if (isMoveToLeft) {
            isMoveToLeft = false;
            ft.setCustomAnimations(R.animator.anim_slide_in_right, R.animator.anim_slide_out_right);
        } else {
            ft.setCustomAnimations(R.animator.anim_slide_in_left, R.animator.anim_slide_out_left);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.dashboard_stock_container, stockFragment);
        ft.commit();
        return isMoveToLeft;
    }

    @Override
    public boolean isHistoricNewReceiptBalanceFragment(Activity activity) {
        if (isFragmentActive(activity, HistoricReceiptBalanceFragment.class,
                R.id.dashboard_stock_container) || isFragmentActive(activity,
                NewReceiptBalanceFragment.class,
                R.id.dashboard_stock_container)) {
            return true;
        }
        return false;

    }

    private boolean isFragmentActive(Activity activity, Class fragmentClass, int layout) {
        Fragment currentFragment = activity.getFragmentManager().findFragmentById(layout);
        if (currentFragment.getClass().equals(fragmentClass)) {
            return true;
        }
        return false;
    }

    @Override
    public void newSurvey(Activity activity) {
        ProgramDB myanmarProgram = ProgramDB.findByUID(activity.getString(R.string.malariaProgramUID));
        ProgramDB stockProgram = ProgramDB.findByUID(activity.getString(R.string.stockProgramUID));
        // Put new survey in session
        SurveyDB survey = new SurveyDB(null, myanmarProgram, Session.getUserDB());
        survey.save();
        Session.setMalariaSurveyDB(survey);
        SurveyDB stockSurvey = new SurveyDB(null, stockProgram, Session.getUserDB(),
                Constants.SURVEY_ISSUE);
        stockSurvey.setEventDate(
                survey.getEventDate());//asociate the malaria survey to the stock survey
        stockSurvey.save();
        Session.setStockSurveyDB(stockSurvey);
        prepareLocationListener(activity, survey);
    }

    @Override
    public void sendSurvey() {
        Session.getMalariaSurveyDB().updateSurveyStatus();
        SurveyDB stockSurvey = Session.getStockSurveyDB();
        if (stockSurvey != null) {
            Session.getStockSurveyDB().complete();
            Date eventDate = new Date();
            saveEventDate(Session.getMalariaSurveyDB(), eventDate);
            saveEventDate(Session.getStockSurveyDB(), eventDate);
            new CompletionSurveyUseCase().execute(Session.getMalariaSurveyDB().getId_survey());
        }
    }

    @Override
    public void completeSurvey() {
        Date eventDate = new Date();
        //Complete malariaSurvey
        SurveyDB survey = Session.getMalariaSurveyDB();
        saveEventDate(survey, eventDate);
        survey.updateSurveyStatus();
        //Complete stockSurvey
        survey = Session.getStockSurveyDB();
        saveEventDate(survey, eventDate);
        survey.complete();
    }

    //The eventDate is used to identify the stock survey for each malaria survey
    //and in quarantine to set the endDate in api queries.
    private void saveEventDate(SurveyDB survey, Date eventDate) {
        survey.setEventDate(eventDate);
        survey.save();
    }

    @Override
    public boolean beforeExit(boolean isBackPressed) {
        SurveyDB malariaSurvey = Session.getMalariaSurveyDB();
        boolean isMalariaBackPressed = beforeExitSurvey(isBackPressed, malariaSurvey);
        SurveyDB stockSurvey = Session.getStockSurveyDB();
        boolean isStockBackPressed = beforeExitSurvey(isBackPressed, stockSurvey);
        if (!isMalariaBackPressed || !isStockBackPressed) {
            return false;
        }
        return isBackPressed;
    }

    private boolean beforeExitSurvey(boolean isBackPressed, SurveyDB survey) {
        if (survey != null) {
            boolean isInProgress = survey.isInProgress();
            survey.getValuesFromDB();
            //Exit + InProgress -> delete
            if (isBackPressed && isInProgress) {
                new SurveyFragmentStrategy().removeSurveysInSession();
                survey.delete();
                isBackPressed = false;
            }
        }
        return isBackPressed;
    }

    public static void onLogoutSuccess() {
        DashboardActivity.dashboardActivity.finishAndGo(LoginActivity.class);
    }
}
