package org.eyeseetea.malariacare.presentation.factory.stock.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.presentation.factory.stock.StockRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.stock.utils.SurveyStock;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class DrugsRowBuilder extends StockRowBuilder {
    public DrugsRowBuilder(String rowTitle, Context context) {
        super(rowTitle, context);
    }

    /**
     * Returns a list with:
     * ["rowMetric", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit",
     * "rowTimeUnit"]
     */
    @Override
    protected List<String> defineColumnClasses() {
        List<String> cssClasses = new ArrayList<>(Constants.STOCK_HISTORY_SIZE);
        cssClasses.add(CSS_ROW_METRIC);
        for (int i = 0; i < Constants.STOCK_HISTORY_SIZE; i++) {
            cssClasses.add(CSS_ROW_TIMEUNIT
            );
        }
        return cssClasses;
    }


    @Override
    protected Object updateColumn(Object currentValue, int newValue, SurveyStock surveyStock) {
        return null;
    }

    @Override
    public void addSurvey(Survey survey) {
        // //Nothing to calculate
    }

    /**
     * Loads drugs strings
     */
    protected Object[] initData() {
        return context.getResources().getStringArray(R.array.stock_table_drugs_strings);
    }

}
