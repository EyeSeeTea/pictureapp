package org.eyeseetea.malariacare.strategies;

import android.app.Activity;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.Session;

import java.util.Date;
import org.eyeseetea.malariacare.fragments.OfflineFragment;


public class DashboardActivityStrategy extends ADashboardActivityStrategy {

    private OfflineFragment mOfflineFragment;

    @Override
    public void reloadStockFragment(Activity activity) {

        mOfflineFragment.reloadHeader(activity);
    }

    @Override
    public boolean showStockFragment(Activity activity, boolean isMoveToLeft) {
        mOfflineFragment = new OfflineFragment();
        return false;
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

    @Override
    public void completeSurvey() {
        Session.getMalariaSurvey().updateSurveyStatus();
    }

    @Override
    public boolean isHistoricNewReceiptBalanceFragment(Activity activity) {
        return false;
    }

    public static void onLogoutSuccess() {
        DashboardActivity.dashboardActivity.finishAndGo(LoginActivity.class);
    }
}
