package org.eyeseetea.malariacare.presentation.factory.stock.rows;


import android.content.Context;

import org.eyeseetea.malariacare.presentation.factory.stock.StockRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.stock.utils.SurveyStock;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manuel on 27/12/16.
 */

public abstract class CounterRowBuilder extends StockRowBuilder {
    public CounterRowBuilder(String rowTitle, Context context) {
        super(rowTitle, context);
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
        int currentCount = (int) currentValue;
        return currentCount + incrementCount(surveyStock, newValue);
    }

    /**
     * Each counterRow fills this function to evaluate if the survey increments the counter or not
     */
    protected abstract int incrementCount(SurveyStock survey, int newValue);
}
