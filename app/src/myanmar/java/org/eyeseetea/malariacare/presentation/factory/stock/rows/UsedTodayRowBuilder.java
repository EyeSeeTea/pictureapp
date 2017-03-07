package org.eyeseetea.malariacare.presentation.factory.stock.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.presentation.factory.stock.utils.SurveyStock;
import org.eyeseetea.malariacare.utils.Utils;

/**
 * Created by manuel on 29/12/16.
 */

public class UsedTodayRowBuilder extends CounterRowBuilder {
    public UsedTodayRowBuilder(Context context) {
        super(context.getResources().getString(R.string.used_today), context);
    }


    @Override
    protected int incrementCount(SurveyStock survey, int newValue) {
        return survey.isIssueSurvey() && isTodaySurvey(survey) ? newValue : 0;
    }

    private boolean isTodaySurvey(SurveyStock survey) {
        return Utils.dateGreaterOrEqualsThanDate(Utils.getTodayDate(),
                survey.getSurvey().getEventDate());
    }


}
