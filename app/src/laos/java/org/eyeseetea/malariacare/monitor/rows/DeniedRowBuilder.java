package org.eyeseetea.malariacare.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.utils.SurveyMonitor;

/**
 * Created by idelcano on 21/07/2016.
 */
public class DeniedRowBuilder extends CounterRowBuilder {
    public DeniedRowBuilder(Context context){
        super(context, context.getString(R.string.monitor_row_title_denied));
    }

    @Override
    protected boolean hasToIncrement(SurveyMonitor surveyMonitor) {
        return surveyMonitor.isDenied();
    }
}
