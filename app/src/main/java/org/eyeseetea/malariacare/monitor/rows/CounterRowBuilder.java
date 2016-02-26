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
 * Abstract counter row that simply increments cell if positive condition
 * Created by arrizabalaga on 26/02/16.
 */
public abstract class CounterRowBuilder extends MonitorRowBuilder {

    public CounterRowBuilder(Context context,String rowTitle){
        super(context, rowTitle);
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
        if(hasToIncrement(survey)){
            return Integer.valueOf(currentCount+1);
        }
        return currentValue;
    }

    /**
     * Each counterRow fills this function to evaluate if the survey increments the counter or not
     * @param survey
     * @return
     */
    protected abstract boolean hasToIncrement(Survey survey);
}
