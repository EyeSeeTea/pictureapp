package org.eyeseetea.malariacare.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.utils.SurveyMonitor;


/**
 * Created by arrizabalaga on 26/02/16.
 */
public class TestedRowBuilder extends CounterRowBuilder {

    public TestedRowBuilder(Context context) {
        super(context, context.getString(R.string.monitor_row_title_tested));
    }

    @Override
    protected Integer incrementCount(SurveyMonitor surveyMonitor) {
        return (surveyMonitor.isTested()) ? 1 : 0;
    }
}
