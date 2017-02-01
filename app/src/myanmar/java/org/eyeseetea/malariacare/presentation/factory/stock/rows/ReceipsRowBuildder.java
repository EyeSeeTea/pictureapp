package org.eyeseetea.malariacare.presentation.factory.stock.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.presentation.factory.stock.utils.SurveyStock;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.Date;

/**
 * Created by manuel on 29/12/16.
 */

public class ReceipsRowBuildder extends CounterRowBuilder {
    public ReceipsRowBuildder(Context context) {
        super(context.getResources().getString(R.string.receipts), context);
    }

    @Override
    protected float incrementCount(SurveyStock surveyStock, float newValue) {
        Survey survey = surveyStock.getSurvey();
        Date maxBalanceDate = Survey.getLastDateForSurveyType(Constants.SURVEY_BALANCE);
        if ((maxBalanceDate == null || Utils.dateGreaterOrEqualsThanDate(maxBalanceDate,
                survey.getEventDate())) && survey.getSurveyType().equals(Constants.SURVEU_RECEIP)) {
            return newValue;
        }
        return 0f;
    }
}
