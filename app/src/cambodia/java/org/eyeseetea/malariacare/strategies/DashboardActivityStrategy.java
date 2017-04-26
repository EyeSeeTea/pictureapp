package org.eyeseetea.malariacare.strategies;

import android.app.Activity;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.SurveyAnsweredRatio;
import org.eyeseetea.malariacare.domain.usecase.GetSurveyAnsweredRatioUseCase;

/**
 * Created by manuel on 28/12/16.
 */

public class DashboardActivityStrategy extends ADashboardActivityStrategy {


    @Override
    public void reloadStockFragment(Activity activity) {

    }

    @Override
    public boolean showStockFragment(Activity activity, boolean isMoveToLeft) {
        return false;
    }

    @Override
    public void newSurvey(Activity activity) {
        Program program = new Select().from(Program.class).querySingle();
        // Put new survey in session
        String orgUnitUid = OrgUnit.findUIDByName(PreferencesState.getInstance().getOrgUnit());
        OrgUnit orgUnit = OrgUnit.findByUID(orgUnitUid);
        Survey survey = new Survey(orgUnit, program, Session.getUser());
        survey.save();
        Session.setMalariaSurvey(survey);
        //Look for coordinates
        prepareLocationListener(activity, survey);
    }

    @Override
    public void sendSurvey() {
        final Survey survey= Session.getMalariaSurvey();
        GetSurveyAnsweredRatioUseCase getSurveyAnsweredRatioUseCase =
                new GetSurveyAnsweredRatioUseCase(survey);
        getSurveyAnsweredRatioUseCase.execute(new GetSurveyAnsweredRatioUseCase.Callback() {
            @Override
            public void onComplete(SurveyAnsweredRatio surveyAnsweredRatio) {
                survey.updateSurveyStatus(surveyAnsweredRatio);
            }
        });

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
        final Survey survey= Session.getMalariaSurvey();
        GetSurveyAnsweredRatioUseCase getSurveyAnsweredRatioUseCase =
                new GetSurveyAnsweredRatioUseCase(survey);
        getSurveyAnsweredRatioUseCase.execute(new GetSurveyAnsweredRatioUseCase.Callback() {
            @Override
            public void onComplete(SurveyAnsweredRatio surveyAnsweredRatio) {
                survey.updateSurveyStatus(surveyAnsweredRatio);
            }
        });
    }

    @Override
    public boolean isHistoricNewReceiptBalanceFragment(Activity activity) {
        return false;
    }

    public static void onLogoutSuccess() {
        DashboardActivity.dashboardActivity.finishAndGo(SettingsActivity.class);
    }
}
