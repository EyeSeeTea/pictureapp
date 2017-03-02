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

package org.eyeseetea.malariacare.presentation.factory.monitor;

import android.content.Context;

import org.apache.commons.lang3.text.WordUtils;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.presentation.factory.monitor.utils.SurveyMonitor;
import org.eyeseetea.malariacare.presentation.factory.monitor.utils.TimePeriodCalculator;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Calculates and holds info related to a row in the monitor section
 * Created by arrizabalaga on 25/02/16.
 */
public abstract class MonitorRowBuilder {

    public static final String ROW_JSON =
            "{\"columnClasses\":[%s],\"columnData\":{\"months\":[%s],\"weeks\":[%s],"
                    + "\"days\":[%s]}}";
    /**
     * Css to style the first column of the table
     */
    protected static final String CSS_ROW_METRIC = "rowMetric";
    /**
     * Css to style a column that refers to a time period
     */
    protected static final String CSS_ROW_TIMEUNIT = "rowTimeUnit";
    /**
     * Css to style a column with a plain value
     */
    protected static final String CSS_ROW_VALUE = "rowValue";
    /**
     * Css to style a column with summary info
     */
    protected static final String CSS_ROW_METRIC_SUMMARY = "rowMetric rowSummary";
    /**
     * Css to style a column with summary info
     */
    protected static final String CSS_ROW_VALUE_SUMMARY = "rowValue rowSummary";
    /**
     * Data (raw value) for each column considering months as timeunit
     */
    protected Object[] monthsData;
    /**
     * Data (raw value) for each column considering weeks as timeunit
     */
    protected Object[] weeksData;
    /**
     * Data (raw value) for each column considering days as timeunit
     */
    protected Object[] daysData;
    /**
     * Context required to translate strings (if needed)
     */
    protected Context context;
    /**
     * Title of the row
     */
    private String rowTitle;
    /**
     * List of css classes for each column of the row
     */
    private List<String> columnClasses;

    public MonitorRowBuilder(Context context, String rowTitle) {
        this.context = context;
        this.rowTitle = rowTitle;
        this.columnClasses = defineColumnClasses();
        this.monthsData = initData();
        this.weeksData = initData();
        this.daysData = initData();
    }

    /**
     * Defines the css classes for each column of the row
     */
    protected abstract List<String> defineColumnClasses();

    /**
     * Calculates the new value of the column considering given survey + current column value
     *
     * @return New value for the same column
     */
    protected abstract Object updateColumn(Object currentValue, SurveyMonitor surveyMonitor);

    /**
     * Returns the title of the row
     */
    public String getRowTitle() {
        return this.rowTitle;
    }

    /**
     * Default value for each column
     */
    protected Object defaultValueColumn() {
        return 0;
    }

    /**
     * Inits a list of defaults values (most of times 0 but it could be a
     */
    private Object[] initData() {
        Object[] data = new Object[Constants.STOCK_HISTORY_SIZE];
        for (int i = 0; i < Constants.STOCK_HISTORY_SIZE; i++) {
            data[i] = defaultValueColumn();
        }
        return data;
    }

    /**
     * Updates row info with the survey
     */
    public void addSurvey(Survey survey) {
        //Null or not sent surveys are not evaluated or surveys with a date from the future
        if (survey == null || survey.getEventDate() == null || survey.getEventDate().after(
                new Date())) {
            return;
        }
        //Update data for each time dimension
        SurveyMonitor surveyMonitor = new SurveyMonitor(survey);
        addSurveyToMonthsData(surveyMonitor);
        addSurveyToWeeksData(surveyMonitor);
        addSurveyToDaysData(surveyMonitor);
    }

    /**
     * Builds a JSON that is inyected via JS into the webview
     */
    public String getRowAsJSON() {
        String rowJSON = String.format(ROW_JSON,
                getColumnClassesAsJSON(),
                getDataCapitalizedAsJSON(monthsData),
                getDataAsJSON(weeksData),
                getDataAsJSON(daysData)
        );
        return rowJSON;
    }

    /**
     * Turns the list of column classes into a list of quoted items.
     * Ex: ["rowMetric","rowValue"] -> "\"rowMetric\",\"rowValue\""
     */
    private String getColumnClassesAsJSON() {
        return convertListStringToJSON(columnClasses);
    }

    /**
     * Turns an array of objects into a list of quoted items (via toString()) adding rowTitle as
     * first item
     */
    private String getDataAsJSON(Object[] data) {
        List<String> dataAsString = new ArrayList<>(Constants.STOCK_HISTORY_SIZE);
        dataAsString.add(this.rowTitle);
        for (int i = 0; i < data.length; i++) {
            dataAsString.add(data[i].toString());
        }
        return convertListStringToJSON(dataAsString);
    }

    /**
     * Turns an array of objects into a list of capitalized quoted items (via toString()) adding
     * rowTitle as
     * first item
     */
    private String getDataCapitalizedAsJSON(Object[] data) {
        List<String> dataAsString = new ArrayList<>(Constants.STOCK_HISTORY_SIZE);
        dataAsString.add(this.rowTitle);
        for (int i = 0; i < data.length; i++) {
            dataAsString.add(WordUtils.capitalize(data[i].toString()));
        }
        return convertListStringToJSON(dataAsString);
    }

    /**
     * Turns the list of strings into a list of quoted items.
     * Ex: ["rowMetric","rowValue"] -> "\"rowMetric\",\"rowValue\""
     */
    private String convertListStringToJSON(List<String> valuesAsString) {
        int numValues = valuesAsString.size();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < numValues; i++) {
            stringBuilder.append("\"" + valuesAsString.get(i) + "\"");
            if (i != (numValues - 1)) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Updates months data according to given survey
     */
    private void addSurveyToMonthsData(SurveyMonitor surveyMonitor) {
        int columnIndex = TimePeriodCalculator.getInstance().findIndexInMonths(
                surveyMonitor.getSurvey().getEventDate());
        //This survey is not relevant to the monitor (too old)
        if (columnIndex == TimePeriodCalculator.COLUMN_NOT_FOUND) {
            return;
        }

        //Updates column considering current value + survey
        this.monthsData[columnIndex] = updateColumn(this.monthsData[columnIndex], surveyMonitor);
    }


    /**
     * Updates months data according to given survey
     */
    private void addSurveyToWeeksData(SurveyMonitor surveyMonitor) {
        int columnIndex = TimePeriodCalculator.getInstance().findIndexInWeeks(
                surveyMonitor.getSurvey().getEventDate());
        //This survey is not relevant to the monitor (too old)
        if (columnIndex == TimePeriodCalculator.COLUMN_NOT_FOUND) {
            return;
        }

        //Updates column considering current value + survey
        this.weeksData[columnIndex] = updateColumn(this.weeksData[columnIndex], surveyMonitor);
    }


    /**
     * Updates months data according to given survey
     */
    private void addSurveyToDaysData(SurveyMonitor surveyMonitor) {
        int columnIndex = TimePeriodCalculator.getInstance().findIndexInDays(
                surveyMonitor.getSurvey().getEventDate());
        //This survey is not relevant to the monitor (too old)
        if (columnIndex == TimePeriodCalculator.COLUMN_NOT_FOUND) {
            return;
        }

        //Updates column considering current value + survey
        this.daysData[columnIndex] = updateColumn(this.daysData[columnIndex], surveyMonitor);
    }


}
