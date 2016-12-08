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
package org.eyeseetea.malariacare.monitor.utils;

import org.eyeseetea.malariacare.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Monitor helper to create time periods
 * Created by arrizabalaga on 25/02/16.
 */
public class TimePeriodCalculator {

    /**
     * Returned value whenever a survey event date is not important to current monitor
     */
    public static final int COLUMN_NOT_FOUND = -1;
    /**
     * Singleton reference to make this usable as such
     */
    private static TimePeriodCalculator instance;
    /**
     * Reference date for today (periods will be calculated from this date into the past
     */
    Date todayDate;
    /**
     * Calculated list of keys for the current time span in months:
     * 201602|201603|201604|201605|201606
     */
    List<String> monthPeriodsKeys;
    /**
     * Calculated list of specific dates for the current time span in months
     */
    List<Date> monthPeriodDates;
    /**
     * Calculated list of keys for the current time span in months:
     * 201550|201551|201552|201601|201602
     */
    List<String> weekPeriodsKeys;
    /**
     * Calculated list of specific dates for the current time span in months
     */
    List<Date> weekPeriodDates;
    /**
     * Calculated list of keys for the current time span in months:
     * 20150224|20150225|20150226|20150227|20150228
     */
    List<String> dayPeriodsKeys;
    /**
     * Calculated list of specific dates for the current time span in months
     */
    List<Date> dayPeriodDates;
    /**
     * Formatter that turns a date into a key representing that month
     */
    private SimpleDateFormat KEY_MONTH_FORMATTER = new SimpleDateFormat("yyyyMM");
    /**
     * Formatter that turns a date into a key representing that week of the year
     */
    private SimpleDateFormat KEY_WEEK_FORMATTER = new SimpleDateFormat("yyyyww");
    /**
     * Formatter that turns a date into a key representing that day
     */
    private SimpleDateFormat KEY_DAY_FORMATTER = new SimpleDateFormat("yyyyMMdd");

    TimePeriodCalculator(Date todayDate) {
        this.todayDate = todayDate;
        init(todayDate);
    }

    /**
     * Singleton getter
     */
    public static TimePeriodCalculator getInstance() {
        if (instance == null) {
            instance = new TimePeriodCalculator(new Date());
        }
        return instance;
    }


    /**
     * Calculates periods according to the given date (supposedly 'today')
     */
    public void init(Date today) {
        todayDate = clearTime(today);
        calculateMonthPeriods(todayDate);
        calculateWeekPeriods(todayDate);
        calculateDayPeriods(todayDate);
    }

    /**
     * Calculates the date corresponding to 6 months in the past
     *
     * @return today - 6 months
     */
    public Date getMinDateForMonitor() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MONTH, -1 * Constants.MONITOR_HISTORY_SIZE);
        return cal.getTime();
    }


    /**
     * Returns the index position where this date belongs according to its month.
     * If the date is out of the current range returns -1.
     */
    public int findIndexInMonths(Date date) {
        if (date == null) {
            return COLUMN_NOT_FOUND;
        }
        String monthKey = getPeriodKeyInMonths(date);
        for (int i = 0; i < monthPeriodsKeys.size(); i++) {
            if (monthKey.equals(monthPeriodsKeys.get(i))) {
                return i;
            }
        }
        return COLUMN_NOT_FOUND;
    }

    /**
     * Returns the index position where this date belongs according to its week.
     * If the date is out of the current range returns -1.
     */
    public int findIndexInWeeks(Date date) {
        if (date == null) {
            return COLUMN_NOT_FOUND;
        }
        String weekKey = getPeriodKeyInWeeks(date);
        for (int i = 0; i < weekPeriodsKeys.size(); i++) {
            if (weekKey.equals(weekPeriodsKeys.get(i))) {
                return i;
            }
        }
        return COLUMN_NOT_FOUND;
    }

    /**
     * Returns the index position where this date belongs according to its date.
     * If the date is out of the current range returns -1.
     */
    public int findIndexInDays(Date date) {
        if (date == null) {
            return COLUMN_NOT_FOUND;
        }
        String dateKey = getPeriodKeyInDays(date);
        for (int i = 0; i < dayPeriodsKeys.size(); i++) {
            if (dateKey.equals(dayPeriodsKeys.get(i))) {
                return i;
            }
        }
        return COLUMN_NOT_FOUND;
    }

    /**
     * Returns the list of dates for the months time unit
     */
    public List<Date> getMonthPeriodDates() {
        return monthPeriodDates;
    }

    /**
     * Returns the list of dates for the months time unit
     */
    public List<Date> getWeekPeriodDates() {
        return weekPeriodDates;
    }

    /**
     * Returns the list of dates for the months time unit
     */
    public List<Date> getDayPeriodDates() {
        return dayPeriodDates;
    }

    /**
     * Calculates a list of keys for the last 6 months [201509, 2001510,201511, 2001512, 201601,
     * 201602]
     *
     * @param today The reference date to build the periods of time
     */
    private void calculateMonthPeriods(Date today) {
        monthPeriodDates = new ArrayList<>(Constants.MONITOR_HISTORY_SIZE);
        monthPeriodsKeys = calculatePeriods(today, Calendar.MONTH, KEY_MONTH_FORMATTER,
                monthPeriodDates);
    }

    /**
     * Calculates a list of keys for the last 6 weeks [201604, 201605, 201606, 201607, 201608,
     * 201609]
     *
     * @param today The reference date to build the periods of time
     */
    private void calculateWeekPeriods(Date today) {
        weekPeriodDates = new ArrayList<>(Constants.MONITOR_HISTORY_SIZE);
        weekPeriodsKeys = calculatePeriods(today, Calendar.WEEK_OF_YEAR, KEY_WEEK_FORMATTER,
                weekPeriodDates);
    }

    /**
     * Calculates a list of keys for the last 6 weeks [201604, 201605, 201606, 201607, 201608,
     * 201609]
     *
     * @param today The reference date to build the periods of time
     */
    private void calculateDayPeriods(Date today) {
        dayPeriodDates = new ArrayList<>(Constants.MONITOR_HISTORY_SIZE);
        dayPeriodsKeys = calculatePeriods(today, Calendar.DATE, KEY_DAY_FORMATTER, dayPeriodDates);
    }

    /**
     * Returns the period key in months for the given date
     * Ex: "2016-08-13 13:45:00" -> "201608"
     */
    private String getPeriodKeyInMonths(Date date) {
        return getPeriodKey(date, KEY_MONTH_FORMATTER);
    }

    /**
     * Returns the period key in weeks for the given date
     * Ex: "2016-01-13 13:45:00" -> "201602"
     */
    private String getPeriodKeyInWeeks(Date date) {
        return getPeriodKey(date, KEY_WEEK_FORMATTER);
    }

    /**
     * Returns the period key in weeks for the given date
     * Ex: "2016-01-13 13:45:00" -> "201602"
     */
    private String getPeriodKeyInDays(Date date) {
        return getPeriodKey(date, KEY_DAY_FORMATTER);
    }

    /**
     * Returns the period key for the given date and formatter
     */
    private String getPeriodKey(Date date, SimpleDateFormat keyPeriodFormatter) {
        if (date == null || keyPeriodFormatter == null) {
            return null;
        }

        return keyPeriodFormatter.format(date);
    }

    /**
     * Create the list of key periods for the given timeUnit
     *
     * @param today        The date from which the periods are calculated
     * @param calendarUnit The calendar unit to move from period to period (months, weeks, days)
     */
    private List<String> calculatePeriods(Date today, int calendarUnit,
            SimpleDateFormat keyFormatter, List<Date> periodDates) {
        List<String> periodKeys = new ArrayList<>(Constants.MONITOR_HISTORY_SIZE);
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        for (int i = 0; i < Constants.MONITOR_HISTORY_SIZE; i++) {
            //Move 1 unit backwards unless first time
            if (i != 0) {
                cal.add(calendarUnit, -1);
            }
            Date iDate = cal.getTime();
            //Get the month key
            String iMonthKey = keyFormatter.format(iDate);
            //Add always at 0
            periodKeys.add(0, iMonthKey);
            periodDates.add(0, iDate);
        }
        return periodKeys;
    }

    /**
     * Wipes off time info from date
     *
     * @return A new date without time data (00:00:00:000)
     */
    private Date clearTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
