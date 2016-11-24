package org.eyeseetea.malariacare.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.utils.SurveyMonitor;

/**
 * Created by idelcano on 07/06/2016.
 */
public class DHAPIPRowBuilder extends CounterRowBuilder {

    public DHAPIPRowBuilder(Context context) {
        super(context, context.getString(R.string.monitor_row_title_dhapip));
    }

    @Override
    protected Integer incrementCount(SurveyMonitor surveyMonitor) {
        return (surveyMonitor.isDHAPIP()) ? 1 : 0;
    }
}
