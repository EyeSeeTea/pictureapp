package org.eyeseetea.malariacare.presentation.factory.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.presentation.factory.monitor.utils.SurveyMonitor;

/**
 * Created by idelcano on 21/07/2016.
 */
public class RDTStockoutRowBuilder extends CounterRowBuilder {

    public RDTStockoutRowBuilder(Context context) {
        super(context, context.getString(R.string.monitor_row_title_rdt_stockout));
    }

    @Override
    protected Integer incrementCount(SurveyMonitor surveyMonitor) {
        return (surveyMonitor.isRDTStockout()) ? 1 : 0;
    }
}

