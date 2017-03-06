package org.eyeseetea.malariacare.presentation.factory.stock.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.presentation.factory.stock.StockRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.stock.utils.SurveyStock;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatusRowBuilder extends StockRowBuilder {
    private float[] balanceSurvey;
    private float[] summationReceiptsUsed;
    public StatusRowBuilder(Context context) {
        super(context.getResources().getString(R.string.status), context);
        balanceSurvey = new float[7];
        summationReceiptsUsed = new float[7];
    }

    @Override
    protected List<String> defineColumnClasses() {
        if (columnClasses == null || columnClasses.isEmpty()) {
            List<String> cssClasses = new ArrayList<>(Constants.STOCK_HISTORY_SIZE);
            cssClasses.add(CSS_ROW_METRIC);

            cssClasses.add(CSS_ROW_EQUALS_IMAGE);
            cssClasses.add(CSS_ROW_EQUALS_IMAGE);
            cssClasses.add(CSS_ROW_EQUALS_IMAGE);
            cssClasses.add(CSS_ROW_EQUALS_IMAGE);
            cssClasses.add(CSS_ROW_EQUALS_IMAGE);
            cssClasses.add(CSS_ROW_EQUALS_IMAGE);
            cssClasses.add(CSS_ROW_EQUALS_IMAGE);


            return cssClasses;
        } else {
            return columnClasses;
        }
    }

    @Override
    protected Object updateColumn(Object currentValue, int newValue, SurveyStock surveyStock) {
        return "";
    }

    @Override
    protected Object updateColumn(Object oldValue, int surveyValue, SurveyStock surveyStock,
            int position) {
        Survey survey = surveyStock.getSurvey();
        Date maxBalanceDate = Survey.getLastDateForSurveyType(Constants.SURVEY_RESET);
        if (maxBalanceDate == null || Utils.dateGreaterOrEqualsThanDate(maxBalanceDate,
                survey.getEventDate())) {
            if (survey.getType() == Constants.SURVEY_RESET) {
                balanceSurvey[position] = surveyValue;
            } else if (survey.getType() == Constants.SURVEY_RECEIPT) {
                summationReceiptsUsed[position] += surveyValue;
            } else if (survey.getType() == Constants.SURVEY_ISSUE) {
                summationReceiptsUsed[position] -= surveyValue;
            }
            columnClasses.set(position + 1, getCSSForValues(position));
        }
        return "";
    }

    @Override
    protected Object defaultValueColumn() {
        return "";
    }

    public String getCSSForValues(int position) {
        float balanceValue = balanceSurvey[position];
        float balancePlusSummation = balanceSurvey[position] + summationReceiptsUsed[position];
        if (balancePlusSummation < balanceValue) {
            return CSS_ROW_LESS_IMAGE;
        } else if (balancePlusSummation > balanceValue) {
            return CSS_ROW_MORE_IMAGE;
        }
        return CSS_ROW_EQUALS_IMAGE;
    }
}
