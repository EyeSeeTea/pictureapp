package org.eyeseetea.malariacare.strategies;

import android.app.Activity;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.BaseActivity;
import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.exception.LoadingNavigationControllerException;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;
import org.eyeseetea.malariacare.fragments.MonitorFragment;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationBuilder;


public class DashboardActivityStrategy extends ADashboardActivityStrategy {


    @Override
    public void reloadStockFragment(Activity activity) {
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).getBaseActivityStrategy().setNotConnectedText(
                    R.string.offline_status);
        }
    }

    @Override
    public void reloadMonitorFragment(Activity activity, MonitorFragment monitorFragment) {
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).getBaseActivityStrategy().setNotConnectedText(
                    R.string.offline_status_online_tab);
        }
    }

    @Override
    public void onUnsentTabSelected(DashboardActivity dashboardActivity) {
        super.onUnsentTabSelected(dashboardActivity);
        dashboardActivity.getBaseActivityStrategy().setNotConnectedText(
                R.string.offline_status);
    }

    @Override
    public void onSentTabSelected(DashboardActivity dashboardActivity) {
        super.onSentTabSelected(dashboardActivity);
        dashboardActivity.getBaseActivityStrategy().setNotConnectedText(
                R.string.offline_status);
    }

    @Override
    public boolean showStockFragment(Activity activity, boolean isMoveToLeft) {
        return false;
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
    public void initNavigationController() throws LoadingNavigationControllerException {
        NavigationBuilder.getInstance().buildController(
                Tab.getFirstTabWithProgram(PreferencesEReferral.getUserProgramId()));
    }
}
