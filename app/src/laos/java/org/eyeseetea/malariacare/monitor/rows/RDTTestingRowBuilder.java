package org.eyeseetea.malariacare.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.utils.SurveyMonitor;

/**
 * Created by idelcano on 21/07/2016.
 */
public class RDTTestingRowBuilder extends CounterRowBuilder {

    public RDTTestingRowBuilder(Context context){
        super(context, context.getString(R.string.monitor_row_title_rdt_testing));
    }

    @Override
    protected Integer incrementCount(SurveyMonitor surveyMonitor) {
        return (surveyMonitor.isRDTTesting())?1:0;
    }
}
