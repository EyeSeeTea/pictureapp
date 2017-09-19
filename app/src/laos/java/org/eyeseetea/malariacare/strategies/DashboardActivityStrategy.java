package org.eyeseetea.malariacare.strategies;

import android.app.Activity;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
public class DashboardActivityStrategy extends ADashboardActivityStrategy {

    public DashboardActivityStrategy(DashboardActivity dashboardActivity) {
        super(dashboardActivity);
    }

    @Override
    public void reloadStockFragment(Activity activity) {

    }

    @Override
    public boolean showStockFragment(Activity activity, boolean isMoveToLeft) {
        return false;
    }

    @Override
    public void newSurvey(Activity activity) {
        ProgramDB program = new Select().from(ProgramDB.class).querySingle();
        // Put new survey in session
        String orgUnitUid = OrgUnitDB.findUIDByName(PreferencesState.getInstance().getOrgUnit());
        OrgUnitDB orgUnitDB = OrgUnitDB.findByUID(orgUnitUid);
        SurveyDB surveyDB = new SurveyDB(orgUnitDB, program, Session.getUserDB());
        surveyDB.save();
        Session.setMalariaSurveyDB(surveyDB);
        //Look for coordinates
        prepareLocationListener(activity, surveyDB);
        mDashboardActivity.initSurvey();
    }

    @Override
    public boolean isHistoricNewReceiptBalanceFragment(Activity activity) {
        return false;
    }


    @Override
    public void sendSurvey() {
        Session.getMalariaSurveyDB().updateSurveyStatus();
    }

    @Override
    public void completeSurvey() {
        Session.getMalariaSurveyDB().updateSurveyStatus();
    }

    @Override
    public boolean beforeExit(boolean isBackPressed) {
        SurveyDB malariaSurveyDB = Session.getMalariaSurveyDB();
        if (malariaSurveyDB != null) {
            boolean isMalariaInProgress = malariaSurveyDB.isInProgress();
            malariaSurveyDB.getValuesFromDB();
            //Exit + InProgress -> delete
            if (isBackPressed && isMalariaInProgress) {
                if (isMalariaInProgress) {
                    Session.setMalariaSurveyDB(null);
                    malariaSurveyDB.delete();
                }
                isBackPressed = false;
            }
        }
        return isBackPressed;
    }

    public static void onLogoutSuccess() {
        DashboardActivity.dashboardActivity.finishAndGo(SettingsActivity.class);
    }
}
