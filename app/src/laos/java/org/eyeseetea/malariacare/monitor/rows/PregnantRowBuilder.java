package org.eyeseetea.malariacare.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.utils.SurveyMonitor;

/**
 * Created by idelcano on 21/07/2016.
 */
public class PregnantRowBuilder extends CounterRowBuilder {
        public PregnantRowBuilder(Context context){
            super(context, context.getString(R.string.monitor_row_title_pregnant));
        }

        @Override
        protected boolean hasToIncrement(SurveyMonitor surveyMonitor) {
            return surveyMonitor.isPregnant();
        }
    }
