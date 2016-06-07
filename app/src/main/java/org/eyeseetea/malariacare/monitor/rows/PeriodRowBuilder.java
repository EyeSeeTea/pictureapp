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
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.monitor.MonitorRowBuilder;
import org.eyeseetea.malariacare.monitor.utils.SurveyMonitor;
import org.eyeseetea.malariacare.monitor.utils.TimePeriodCalculator;
import org.eyeseetea.malariacare.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Builds the header with the time columns
 * Created by arrizabalaga on 26/02/16.
 */
public class PeriodRowBuilder extends MonitorRowBuilder{

    /**
     * Date format for months periods: 'Jan'
     */
    //private static final String MONTH_FORMAT="MMM";
    private static String MONTHS_FORMAT="'%s' MM";

    /**
     * Date format for weeks periods 'Week 09'
     */
    private static String WEEKS_FORMAT="'%s' ww";

    /**
     * Date format for days periods: 'Wed'
     */
    //private static final String DAYS_FORMAT="EEE";
    private static String DAYS_FORMAT="'%s' dd";

    public PeriodRowBuilder(Context context){
        super(context,context.getString(R.string.monitor_row_title_period));
        initWeekFormat();
        initDayFormat();
        initMonthFormat();
        initData();
    }

    /**
     * Returns a list with:
     * ["rowMetric", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit", "rowTimeUnit"]
     * @return
     */
    @Override
    protected List<String> defineColumnClasses() {
        List<String> cssClasses = new ArrayList<>(Constants.MONITOR_HISTORY_SIZE);
        cssClasses.add(CSS_ROW_METRIC);
        for(int i=0;i<Constants.MONITOR_HISTORY_SIZE;i++){
            cssClasses.add(CSS_ROW_TIMEUNIT);
        }
        return cssClasses;
    }

    /**
     * Override default addSurvey since this row is static
     * @param survey
     */
    @Override
    public void addSurvey(Survey survey){
        //Nothing to calculate
    }

    /**
     * This is a special row where survey is not important
     * @param currentValue
     * @param surveyMonitor
     * @return
     */
    @Override
    protected Object updateColumn(Object currentValue, SurveyMonitor surveyMonitor) {
        return null;
    }

    private void initWeekFormat(){
        WEEKS_FORMAT = String.format(WEEKS_FORMAT,context.getString(R.string.monitor_row_title_week));
    }

    private void initDayFormat(){
        DAYS_FORMAT = String.format(DAYS_FORMAT,context.getString(R.string.monitor_row_title_day));
    }

    private void initMonthFormat(){
        MONTHS_FORMAT = String.format(MONTHS_FORMAT,context.getString(R.string.monitor_row_title_month));
    }

    /**
     * Loads period times info
     */
    private void initData(){
        initMonthsData();
        initWeeksData();
        initDaysData();
    }

    /**
     * Inits columns for months: 'Jan','Feb','Mar',...
     */
    private void initMonthsData(){
        List<Date> monthsDates= TimePeriodCalculator.getInstance().getMonthPeriodDates();
        initTimeData(monthsDates, new SimpleDateFormat(MONTHS_FORMAT), monthsData);
    }

    /**
     * Inits columns for weeks: 'Week 1','Week 2','Week 3',...
     */
    private void initWeeksData(){
        List<Date> weeksDates= TimePeriodCalculator.getInstance().getWeekPeriodDates();
        initTimeData(weeksDates,new SimpleDateFormat(WEEKS_FORMAT),weeksData);
    }

    /**
     * Inits columns for days: 'Mon','Tue','Wed',...
     */
    private void initDaysData(){
        List<Date> daysDates= TimePeriodCalculator.getInstance().getDayPeriodDates();
        initTimeData(daysDates,new SimpleDateFormat(DAYS_FORMAT),daysData);
    }

    /**
     * Fill the given data array with the dates formatted via formatter
     * @param dates
     * @param formatter
     * @param data
     */
    private void initTimeData(List<Date> dates, SimpleDateFormat formatter, Object[] data){
        for(int i=0;i<Constants.MONITOR_HISTORY_SIZE;i++){
            data[i]=formatter.format(dates.get(i));
        }
    }
}
