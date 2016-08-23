package org.eyeseetea.malariacare.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.utils.SurveyMonitor;

/**
 * Created by idelcano on 21/07/2016.
 */
public class SubmissionRowBuilder extends CounterRowBuilder {

    public SubmissionRowBuilder(Context context){
        super(context, context.getString(R.string.monitor_row_title_submission));
    }

    @Override
    protected Integer incrementCount(SurveyMonitor surveyMonitor) {
        return (surveyMonitor.isSubmission())?1:0;
    }
}
