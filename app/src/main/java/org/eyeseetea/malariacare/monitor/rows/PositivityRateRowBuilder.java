/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.eyeseetea.malariacare.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.monitor.utils.PositivityRate;
import org.eyeseetea.malariacare.monitor.utils.SurveyMonitor;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arrizabalaga on 26/02/16.
 */
public class PositivityRateRowBuilder extends MonitorRowBuilder {

    public PositivityRateRowBuilder(Context context){
        super(context, context.getString(R.string.monitor_row_title_positivity_rate));
    }

    /**
     * Returns a list with column classes
     * @return
     */
    @Override
    protected List<String> defineColumnClasses() {
        List<String> cssClasses = new ArrayList<>(Constants.MONITOR_HISTORY_SIZE+1);
        cssClasses.add(CSS_ROW_METRIC_SUMMARY);
        for(int i=0;i<Constants.MONITOR_HISTORY_SIZE;i++){
            cssClasses.add(CSS_ROW_VALUE_SUMMARY);
        }
        return cssClasses;
    }

    /**
     * This row is special, instead of counter there are rates
     * @return
     */
    protected Object defaultValueColumn(){
        return new PositivityRate();
    }

    @Override
    protected Object updateColumn(Object currentValue, SurveyMonitor surveyMonitor) {
        PositivityRate positivityRate = (PositivityRate) currentValue;
        if(surveyMonitor.isRated()){
            positivityRate.incNumSuspected();
        }
        if(surveyMonitor.isPositive()){
            positivityRate.incNumPositive();
        }
        return positivityRate;
    }
}
