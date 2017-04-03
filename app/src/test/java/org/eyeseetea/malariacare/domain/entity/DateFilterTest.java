package org.eyeseetea.malariacare.domain.entity;

import static junit.framework.Assert.assertFalse;

import static org.junit.Assert.assertTrue;

import org.eyeseetea.malariacare.domain.usecase.DateFilter;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFilterTest {
    DateFilter mSurveyFilter;
    String mockedTodayDate = "2017-12-20";

    String thisWeekStart = "2017-12-18";
    String thisWeekEnd = "2017-12-24";

    String thisMonthStart = "2017-11-01";
    String thisMonthEnd = "2017-12-30";

    String lastWeekStart = "2017-12-11";
    String lastWeekEnd = "2017-12-17";

    String lastMonthStart = "2017-10-01";
    String lastMonthEnd = "2017-11-30";

    String last6DaysStart = "2017-12-15";
    String last6DaysEnd = "2017-12-20";

    String last6WeeksStart = "2017-11-13";
    String last6WeeksEnd = "2017-12-24";

    String last6MonthsStart = "2017-05-01";
    String last6MonthsEnd = "2017-11-30";

    @Before
    public void setup() {
        mSurveyFilter = new DateFilter();
    }

    public Calendar getFakeTodayCalendar() {
        return toCalendar(getDateFromString(mockedTodayDate));
    }

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public Date getDateFromString(String dateAsString) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = null;
        try {
            date =  format.parse(dateAsString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public Date getToDateAfterDays(Date date, Integer day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        return cal.getTime();
    }

    @Test
    public void testThisWeek() {
        mSurveyFilter.setThisWeek(true);
        Date startDate = getDateFromString(thisWeekStart);
        Date endDate = getDateFromString(thisWeekEnd);
        testDates(startDate, endDate);
    }

    @Test
    public void testThisMonth() {
        mSurveyFilter.setThisMonth(true);
        Date startDate = getDateFromString(thisMonthStart);
        Date endDate = getDateFromString(thisMonthEnd);
        testDates(startDate, endDate);
    }

    @Test
    public void testLastWeek() {
        mSurveyFilter.setLastWeek(true);
        Date startDate = getDateFromString(lastWeekStart);
        Date endDate = getDateFromString(lastWeekEnd);
        testDates(startDate, endDate);
    }

    @Test
    public void testLastMonth() {
        mSurveyFilter.setLastMonth(true);
        Date startDate = getDateFromString(lastMonthStart);
        Date endDate = getDateFromString(lastMonthEnd);
        testDates(startDate, endDate);
    }

    @Test
    public void testLast6Days() {
        mSurveyFilter.setLast6Days(true);
        Date startDate = getDateFromString(last6DaysStart);
        Date endDate = getDateFromString(last6DaysEnd);
        testDates(startDate, endDate);
    }

    @Test
    public void testLast6Weeks() {
        mSurveyFilter.setLast6Weeks(true);
        Date startDate = getDateFromString(last6WeeksStart);
        Date endDate = getDateFromString(last6WeeksEnd);
        testDates(startDate, endDate);
    }

    @Test
    public void testLast6Months() {
        mSurveyFilter.setLast6Month(true);
        Date startDate = getDateFromString(last6MonthsStart);
        Date endDate = getDateFromString(last6MonthsEnd);
        testDates(startDate, endDate);
    }

    private void testDates(Date startDate, Date endDate) {
        Date startDateOut = getToDateAfterDays(startDate, -1);
        Date endDateOut = getToDateAfterDays(endDate, 1);

        Date filterStart = mSurveyFilter.getStartFilterDate(getFakeTodayCalendar());
        Date filterEnd = mSurveyFilter.getEndFilterDate(getFakeTodayCalendar());
        assertTrue(mSurveyFilter.isDateBetweenDates(startDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(startDateOut, filterStart, filterEnd));
        assertTrue(mSurveyFilter.isDateBetweenDates(endDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(endDateOut, filterStart, filterEnd));
    }
}
