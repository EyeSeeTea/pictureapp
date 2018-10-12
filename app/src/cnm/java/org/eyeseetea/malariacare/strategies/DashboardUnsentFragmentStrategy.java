package org.eyeseetea.malariacare.strategies;


import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

import java.util.Date;

public class DashboardUnsentFragmentStrategy extends ADashboardUnsentFragmentStrategy {
    public static void deleteSurvey(SurveyDB surveyDB) {
        Context context = PreferencesState.getInstance().getContext();
        Date date = surveyDB.getEventDate();
        surveyDB.delete();
        SurveyDB stockSurvey = SurveyDB.findSurveysWithProgramAndEventDate(
                context.getResources().getString(R.string.stock_program_uid), date);
        if(stockSurvey!=null) {
            stockSurvey.delete();
        }
    }
}
