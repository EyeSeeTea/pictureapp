package org.eyeseetea.malariacare.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.monitor.utils.PositivityRate;
import org.eyeseetea.malariacare.monitor.utils.SurveyStats;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arrizabalaga on 26/02/16.
 */
public class PositivityRateRowBuilder extends MonitorRowBuilder {

    public PositivityRateRowBuilder(Context context){
        super(context, context.getString(R.string.monitor_row_title_positivity_rate));
    }

    /**
     * Returns a list with column classes
     * @return
     */
    @Override
    protected List<String> defineColumnClasses() {
        List<String> cssClasses = new ArrayList<>(Constants.MONITOR_HISTORY_SIZE+1);
        cssClasses.add(CSS_ROW_METRIC_SUMMARY);
        for(int i=0;i<Constants.MONITOR_HISTORY_SIZE;i++){
            cssClasses.add(CSS_ROW_VALUE_SUMMARY);
        }
        return cssClasses;
    }

    /**
     * This row is special, instead of counter there are rates
     * @return
     */
    protected Object defaultValueColumn(){
        return new PositivityRate();
    }

    @Override
    protected Object updateColumn(Object currentValue, Survey survey) {
        PositivityRate positivityRate = (PositivityRate) currentValue;
        if(SurveyStats.isSuspected(survey)){
            positivityRate.incNumSuspected();
        }
        if(SurveyStats.isPositive(survey)){
            positivityRate.incNumPositive();
        }
        return positivityRate;
    }
}
