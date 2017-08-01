package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Survey;
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
        Program myanmarProgram = Program.findByUID(activity.getString(R.string.malariaProgramUID));
        Program stockProgram = Program.findByUID(activity.getString(R.string.stockProgramUID));
        // Put new survey in session
        Survey survey = new Survey(null, myanmarProgram, Session.getUser());
        survey.save();
        Session.setMalariaSurvey(survey);
        Survey stockSurvey = new Survey(null, stockProgram, Session.getUser(),
                Constants.SURVEY_ISSUE);
        stockSurvey.setEventDate(
                survey.getEventDate());//asociate the malaria survey to the stock survey
        stockSurvey.save();
        Session.setStockSurvey(stockSurvey);
        prepareLocationListener(activity, survey);
    }

    @Override
    public void sendSurvey() {
        Session.getMalariaSurvey().updateSurveyStatus();
        Survey stockSurvey = Session.getStockSurvey();
        if (stockSurvey != null) {
            Session.getStockSurvey().complete();
            Date eventDate = new Date();
            saveEventDate(Session.getMalariaSurvey(), eventDate);
            saveEventDate(Session.getStockSurvey(), eventDate);
            new CompletionSurveyUseCase().execute(Session.getMalariaSurvey().getId_survey());
        }
    }

    @Override
    public void completeSurvey() {
        Date eventDate = new Date();
        //Complete malariaSurvey
        Survey survey = Session.getMalariaSurvey();
        saveEventDate(survey, eventDate);
        survey.updateSurveyStatus();
        //Complete stockSurvey
        survey = Session.getStockSurvey();
        saveEventDate(survey, eventDate);
        survey.complete();
    }

    //The eventDate is used to identify the stock survey for each malaria survey
    //and in quarantine to set the endDate in api queries.
    private void saveEventDate(Survey survey, Date eventDate) {
        survey.setEventDate(eventDate);
        survey.save();
    }

    @Override
    public boolean beforeExit(boolean isBackPressed) {
        Survey malariaSurvey = Session.getMalariaSurvey();
        boolean isMalariaBackPressed = beforeExitSurvey(isBackPressed, malariaSurvey);
        Survey stockSurvey = Session.getStockSurvey();
        boolean isStockBackPressed = beforeExitSurvey(isBackPressed, stockSurvey);
        if (!isMalariaBackPressed || !isStockBackPressed) {
            return false;
        }
        return isBackPressed;
    }

    private boolean beforeExitSurvey(boolean isBackPressed, Survey survey) {
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
