package org.eyeseetea.malariacare.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.monitor.utils.SurveyStats;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arrizabalaga on 26/02/16.
 */
public class ASMQRowBuilder extends CounterRowBuilder {

    public ASMQRowBuilder(Context context){
        super(context, context.getString(R.string.monitor_row_title_asmq));
    }

    @Override
    protected boolean hasToIncrement(Survey survey) {
        return SurveyStats.isASMQ(survey);
    }
}
