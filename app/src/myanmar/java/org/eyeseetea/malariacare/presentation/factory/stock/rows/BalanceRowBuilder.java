package org.eyeseetea.malariacare.presentation.factory.stock.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.presentation.factory.stock.StockRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.stock.utils.SurveyStock;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by manuel on 29/12/16.
 */

public class BalanceRowBuilder extends StockRowBuilder {
    public BalanceRowBuilder(Context context) {
        super(context.getResources().getString(R.string.stock_balance), context);
    }


    /**
     * Returns a list with:
     * ["rowMetric", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit",
     * "rowTimeUnit", "rowTimeUnit"]
     */
    @Override
    protected List<String> defineColumnClasses() {
        List<String> cssClasses = new ArrayList<>(Constants.STOCK_HISTORY_SIZE);
        cssClasses.add(CSS_ROW_METRIC);
        for (int i = 0; i < Constants.STOCK_HISTORY_SIZE; i++) {
            cssClasses.add(CSS_ROW_VALUE);
        }
        return cssClasses;
    }

    @Override
    protected Object updateColumn(Object currentValue, int newValue, SurveyStock surveyStock) {
        Survey survey = surveyStock.getSurvey();
        Date maxBalanceDate = Survey.getLastDateForSurveyType(Constants.SURVEY_RESET);
        if (survey.getType().equals(Constants.SURVEY_RESET) &&
                (maxBalanceDate == null || maxBalanceDate.equals(
                        survey.getEventDate()))) {
            return newValue;
        }
        return currentValue;

    }
}
