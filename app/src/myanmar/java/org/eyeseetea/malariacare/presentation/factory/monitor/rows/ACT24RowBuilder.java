package org.eyeseetea.malariacare.presentation.factory.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.SurveyQuestionTreatmentValue;
import org.eyeseetea.malariacare.presentation.factory.monitor.utils.SurveyMonitor;

/**
 * Created by idelcano on 21/07/2016.
 */
public class ACT24RowBuilder extends CounterRowBuilder {
    public ACT24RowBuilder(Context context) {
        super(context, context.getString(R.string.ACT_x_24));
    }

    @Override
    protected Integer incrementCount(SurveyMonitor surveyMonitor) {
        return Math.round(Float.parseFloat(
                new SurveyQuestionTreatmentValue(surveyMonitor.getSurvey()).getACT24Value()));
    }
}