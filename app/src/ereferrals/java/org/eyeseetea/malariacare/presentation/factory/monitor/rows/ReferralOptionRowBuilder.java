package org.eyeseetea.malariacare.presentation.factory.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.presentation.factory.monitor.utils.SurveyMonitor;

/**
 * Created by idelcano on 21/07/2016.
 */
public class ReferralOptionRowBuilder extends CounterRowBuilder {

    public ReferralOptionRowBuilder(Context context) {
        super(context, "");
    }

    @Override
    protected Integer incrementCount(SurveyMonitor surveyMonitor) {
        return (surveyMonitor.isReferral()) ? 1 : 0;
    }
}
