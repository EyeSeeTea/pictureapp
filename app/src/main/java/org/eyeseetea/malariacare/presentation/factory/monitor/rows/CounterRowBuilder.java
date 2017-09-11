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
package org.eyeseetea.malariacare.presentation.factory.monitor.rows;

import android.content.Context;

import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.utils.SurveyMonitor;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract counter row that simply increments cell if positive condition
 * Created by arrizabalaga on 26/02/16.
 */
public abstract class CounterRowBuilder extends MonitorRowBuilder {

    public CounterRowBuilder(Context context, String rowTitle) {
        super(context, rowTitle);
    }

    /**
     * Returns a list with:
     * ["rowMetric", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit",
     * "rowTimeUnit"]
     */
    @Override
    protected List<String> defineColumnClasses() {
        List<String> cssClasses = new ArrayList<>(Constants.MONITOR_HISTORY_SIZE);
        cssClasses.add(CSS_ROW_METRIC);
        for (int i = 0; i < Constants.MONITOR_HISTORY_SIZE; i++) {
            cssClasses.add(CSS_ROW_VALUE);
        }
        return cssClasses;
    }

    @Override
    protected Object updateColumn(Object currentValue, SurveyMonitor surveyMonitor) {
        Integer currentCount = (Integer) currentValue;
        return Integer.valueOf(currentCount + incrementCount(surveyMonitor));
    }

    /**
     * Each counterRow fills this function to evaluate if the survey increments the counter or not
     */
    protected abstract Integer incrementCount(SurveyMonitor survey);
}
