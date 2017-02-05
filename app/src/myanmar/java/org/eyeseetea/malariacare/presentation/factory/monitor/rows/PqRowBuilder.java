package org.eyeseetea.malariacare.presentation.factory.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.SurveyQuestionValue;
import org.eyeseetea.malariacare.presentation.factory.monitor.utils.SurveyMonitor;

/**
 * Created by idelcano on 21/07/2016.
 */
public class PqRowBuilder extends CounterRowBuilder {

    public PqRowBuilder(Context context) {
        super(context, context.getString(R.string.Pq));
    }

    @Override
    protected Integer incrementCount(SurveyMonitor surveyMonitor) {
        return Math.round(Float.parseFloat(new SurveyQuestionValue(surveyMonitor.getSurvey()).getPqValue()));
    }
}
