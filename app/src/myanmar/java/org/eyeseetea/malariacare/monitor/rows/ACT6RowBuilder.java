package org.eyeseetea.malariacare.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.utils.SurveyMonitor;

/**
 * Created by idelcano on 21/07/2016.
 */
public class ACT6RowBuilder extends CounterRowBuilder {

    public ACT6RowBuilder(Context context) {
        super(context, context.getString(R.string.ACT_x_6));
    }

    @Override
    protected Integer incrementCount(SurveyMonitor surveyMonitor) {
        if (surveyMonitor.isACT6() == null) {
            return null;
        }
        return (surveyMonitor.isACT6()) ? 1 : 0;
    }
}
