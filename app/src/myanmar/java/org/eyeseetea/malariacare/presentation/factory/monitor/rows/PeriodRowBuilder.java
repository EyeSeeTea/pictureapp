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

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.presentation.factory.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.presentation.factory.monitor.utils.SurveyMonitor;
import org.eyeseetea.malariacare.presentation.factory.monitor.utils.TimePeriodCalculator;
import org.eyeseetea.malariacare.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Builds the header with the time columns
 * Created by arrizabalaga on 26/02/16.
 */
public class PeriodRowBuilder extends MonitorRowBuilder {

    /**
     * Date format for months periods: 'Jan'
     */
    //private static final String MONTH_FORMAT="MMM";
    private static String MONTHS_NUMBER_FORMAT = "MM";
    private static String MONTHS_TEXT_FORMAT = "MMMM";

    /**
     * Date format for weeks periods 'Week 09'
     */
    private static String WEEKS_FORMAT = "'%s' ww";

    /**
     * Date format for days periods: 'Wed'
     */
    //private static final String DAYS_FORMAT="EEE";
    private static String DAYS_FORMAT = "'%s' dd";

    public PeriodRowBuilder(Context context, int titleId) {
        super(context, context.getString(titleId));
        initWeekFormat();
        initDayFormat();
        initData();
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
            cssClasses.add(CSS_ROW_TIMEUNIT);
        }
        return cssClasses;
    }

    /**
     * Override default addSurvey since this row is static
     */
    @Override
    public void addSurvey(Survey survey) {
        //Nothing to calculate
    }

    /**
     * This is a special row where survey is not important
     */
    @Override
    protected Object updateColumn(Object currentValue, SurveyMonitor surveyMonitor) {
        return null;
    }

    private void initWeekFormat() {
        WEEKS_FORMAT = String.format(WEEKS_FORMAT,
                context.getString(R.string.monitoring_row_title_week));
    }

    private void initDayFormat() {
        DAYS_FORMAT = String.format(DAYS_FORMAT,
                context.getString(R.string.monitoring_row_title_day));
    }

    /**
     * Loads period times info
     */
    private void initData() {
        initMonthsData();
        initWeeksData();
        initDaysData();
    }

    /**
     * Inits columns for months: 'Jan','Feb','Mar',...
     */
    private void initMonthsData() {
        List<Date> monthsDates = TimePeriodCalculator.getInstance().getMonthPeriodDates();
        initTimeDataForMonth(monthsDates,
                new SimpleDateFormat(MONTHS_NUMBER_FORMAT, Locale.ENGLISH),
                new SimpleDateFormat(MONTHS_TEXT_FORMAT, Locale.ENGLISH), monthsData);
    }

    /**
     * Inits columns for weeks: 'Week 1','Week 2','Week 3',...
     */
    private void initWeeksData() {
        List<Date> weeksDates = TimePeriodCalculator.getInstance().getWeekPeriodDates();
        initTimeData(weeksDates, new SimpleDateFormat(WEEKS_FORMAT), weeksData);
    }

    /**
     * Inits columns for days: 'Mon','Tue','Wed',...
     */
    private void initDaysData() {
        List<Date> daysDates = TimePeriodCalculator.getInstance().getDayPeriodDates();
        initTimeData(daysDates, new SimpleDateFormat(DAYS_FORMAT), daysData);
    }

    /**
     * Fill the given data array with the dates formatted via formatter
     */
    private void initTimeData(List<Date> dates, SimpleDateFormat formatter, Object[] data) {
        for (int i = 0; i < Constants.MONITOR_HISTORY_SIZE; i++) {
            data[i] = formatter.format(dates.get(i));
        }
    }

    private void initTimeDataForMonth(List<Date> dates, SimpleDateFormat numberFormatter,
            SimpleDateFormat textFormatter, Object[] data) {
        for (int i = 0; i < Constants.MONITOR_HISTORY_SIZE; i++) {
            data[i] = getInternationalizeMonth(numberFormatter.format(dates.get(i)),
                    textFormatter.format(dates.get(i)));
        }
    }

    private String getInternationalizeMonth(String monthNumber, String monthText) {
        String monthStringName = "monitoring_month_%s_%s";
        String monthTextResult = "";

        if (monthNumber != null && monthText != null) {
            Context context = PreferencesState.getInstance().getContext();

            monthStringName = String.format(monthStringName, monthNumber, monthText);

            int identifier = context.getResources().getIdentifier(monthStringName, "string",
                    context.getPackageName());

            //if the id is 0 it not exist.
            if (identifier != 0) {
                monthTextResult = context.getString(identifier);
            }
        } else {
            monthTextResult = monthText;
        }

        return monthTextResult;
    }
}
