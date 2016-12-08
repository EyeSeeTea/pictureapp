package org.eyeseetea.malariacare.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.utils.SurveyMonitor;

/**
 * Created by idelcano on 21/07/2016.
 */
public class ACT18RowBuilder extends CounterRowBuilder {

    public ACT18RowBuilder(Context context) {
        super(context, context.getString(R.string.monitor_row_title_act6x3));
    }

    @Override
    protected Integer incrementCount(SurveyMonitor surveyMonitor) {
        return (surveyMonitor.isACT18()) ? 1 : 0;
    }
}