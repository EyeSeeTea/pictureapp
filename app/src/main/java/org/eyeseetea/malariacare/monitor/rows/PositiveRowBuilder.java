package org.eyeseetea.malariacare.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.monitor.utils.SurveyStats;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arrizabalaga on 26/02/16.
 */
public class PositiveRowBuilder extends MonitorRowBuilder {

    public PositiveRowBuilder(Context context){
        super(context, context.getString(R.string.monitor_row_title_positive));
    }

    /**
     * Returns a list with:
     * ["rowMetric", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit"]
     * @return
     */
    @Override
    protected List<String> defineColumnClasses() {
        List<String> cssClasses = new ArrayList<>(Constants.MONITOR_HISTORY_SIZE);
        cssClasses.add(CSS_ROW_METRIC);
        for(int i=0;i<Constants.MONITOR_HISTORY_SIZE;i++){
            cssClasses.add(CSS_ROW_VALUE);
        }
        return cssClasses;
    }

    @Override
    protected Object updateColumn(Object currentValue, Survey survey) {
        Integer currentCount=(Integer)currentValue;
        if(SurveyStats.isPositive(survey)){
            return Integer.valueOf(currentCount+1);
        }
        return currentValue;
    }
}
