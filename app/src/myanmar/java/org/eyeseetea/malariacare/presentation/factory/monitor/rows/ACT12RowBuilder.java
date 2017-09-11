package org.eyeseetea.malariacare.presentation.factory.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.SurveyQuestionTreatmentValue;
import org.eyeseetea.malariacare.presentation.factory.monitor.utils.SurveyMonitor;

/**
 * Created by idelcano on 21/07/2016.
 */
public class ACT12RowBuilder extends CounterRowBuilder {

    public ACT12RowBuilder(Context context) {
        super(context, context.getString(R.string.ACT_x_12));
    }

    @Override
    protected Integer incrementCount(SurveyMonitor surveyMonitor) {
        return Math.round(Float.parseFloat(
                new SurveyQuestionTreatmentValue(surveyMonitor.getSurvey()).getACT12Value()));
    }
}