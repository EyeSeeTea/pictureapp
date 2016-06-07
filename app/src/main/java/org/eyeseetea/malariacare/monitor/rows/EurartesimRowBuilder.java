package org.eyeseetea.malariacare.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.utils.SurveyMonitor;

/**
 * Created by idelcano on 07/06/2016.
 */
public class EurartesimRowBuilder  extends CounterRowBuilder {

    public EurartesimRowBuilder(Context context){
        super(context, context.getString(R.string.monitor_row_title_eurartesim));
    }

    @Override
    protected boolean hasToIncrement(SurveyMonitor surveyMonitor) {
        return surveyMonitor.isEurartesim();
    }
}
