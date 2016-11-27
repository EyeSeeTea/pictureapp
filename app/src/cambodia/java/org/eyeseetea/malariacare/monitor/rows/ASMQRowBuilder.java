package org.eyeseetea.malariacare.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.utils.SurveyMonitor;

/**
 * Created by idelcano on 07/06/2016.
 */
public class ASMQRowBuilder extends CounterRowBuilder {

    public ASMQRowBuilder(Context context) {
        super(context, context.getString(R.string.monitor_row_title_asmq));
    }

    @Override
    protected Integer incrementCount(SurveyMonitor surveyMonitor) {
        return (surveyMonitor.isASMQ()) ? 1 : 0;
    }
}
