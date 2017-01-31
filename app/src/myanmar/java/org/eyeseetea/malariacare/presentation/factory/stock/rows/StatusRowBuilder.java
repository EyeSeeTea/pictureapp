package org.eyeseetea.malariacare.presentation.factory.stock.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.presentation.factory.stock.StockRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.stock.utils.SurveyStock;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manuel on 29/12/16.
 */

public class StatusRowBuilder extends StockRowBuilder {
    public StatusRowBuilder(Context context) {
        super(context.getResources().getString(R.string.status), context);
    }

    @Override
    protected List<String> defineColumnClasses() {
        List<String> cssClasses = new ArrayList<>(Constants.STOCK_HISTORY_SIZE);
        cssClasses.add(CSS_ROW_METRIC);

        cssClasses.add(CSS_ROW_EQUALS_IMAGE);
        cssClasses.add(CSS_ROW_LESS_IMAGE);
        cssClasses.add(CSS_ROW_MORE_IMAGE);
        cssClasses.add(CSS_ROW_LESS_IMAGE);
        cssClasses.add(CSS_ROW_EQUALS_IMAGE);
        cssClasses.add(CSS_ROW_MORE_IMAGE);
        cssClasses.add(CSS_ROW_EQUALS_IMAGE);


        return cssClasses;
    }

    @Override
    protected Object updateColumn(Object currentValue, float newValue, SurveyStock surveyStock) {
        return "";
    }

    @Override
    protected Object defaultValueColumn() {
        return "";
    }
}
