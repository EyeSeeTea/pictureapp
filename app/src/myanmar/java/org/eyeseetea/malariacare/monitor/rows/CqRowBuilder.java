package org.eyeseetea.malariacare.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.utils.SurveyMonitor;

/**
 * Created by idelcano on 21/07/2016.
 */
public class CqRowBuilder extends CounterRowBuilder {

    @Override
    protected Object defaultValueColumn() {
        //// TODO: 14/12/2016 Remove it 
        return SurveyMonitor.DEFAULT_INVALID_MONITOR_VALUE;
    }

    public CqRowBuilder(Context context) {
        super(context, context.getString(R.string.Cq));
    }

    @Override
    protected Integer incrementCount(SurveyMonitor surveyMonitor) {
        return (surveyMonitor.isCq()) ? 1 : 0;
    }
}
