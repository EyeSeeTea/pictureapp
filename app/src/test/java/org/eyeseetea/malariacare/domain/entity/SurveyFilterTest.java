package org.eyeseetea.malariacare.domain.entity;

import static junit.framework.Assert.assertFalse;

import static org.junit.Assert.assertTrue;

import org.eyeseetea.malariacare.domain.usecase.SurveyFilter;
import org.junit.Before;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SurveyFilterTest {
    SurveyFilter mSurveyFilter;
    String mockedTodayDate = "2017-12-20";

    String thisWeekStart = "2017-12-13";
    String thisWeekEnd = "2017-12-20";

    String thisMonthStart = "2017-11-20";
    String thisMonthEnd = "2017-12-20";

    String lastWeekStart = "2017-12-06";
    String lastWeekEnd = "2017-12-13";

    String lastMonthStart = "2017-10-20";
    String lastMonthEnd = "2017-11-20";

    String last6DaysStart = "2017-12-14";
    String last6DaysEnd = "2017-12-20";

    String last6WeeksStart = "2017-11-08";
    String last6WeeksEnd = "2017-12-20";

    String last6MonthsStart = "2017-06-20";
    String last6MonthsEnd = "2017-12-20";

    @Before
    public void setup() {
        mSurveyFilter = new SurveyFilter();
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
            date = (Date) format.parse(dateAsString);
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
        Date startDateOut = getToDateAfterDays(startDate, -1);
        Date endDate = getDateFromString(thisWeekEnd);
        Date endDateOut = getToDateAfterDays(endDate, 1);

        Date filterStart = mSurveyFilter.getStartFilterDate(getFakeTodayCalendar());
        Date filterEnd = mSurveyFilter.getEndFilterDate(getFakeTodayCalendar());
        assertTrue(mSurveyFilter.isDateBetweenDates(startDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(startDateOut, filterStart, filterEnd));
        assertTrue(mSurveyFilter.isDateBetweenDates(endDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(endDateOut, filterStart, filterEnd));
    }

    @Test
    public void testThisMonth() {
        mSurveyFilter.setThisMonth(true);
        Date startDate = getDateFromString(thisMonthStart);
        Date startDateOut = getToDateAfterDays(startDate, -1);
        Date endDate = getDateFromString(thisMonthEnd);
        Date endDateOut = getToDateAfterDays(endDate, 1);

        Date filterStart = mSurveyFilter.getStartFilterDate(getFakeTodayCalendar());
        Date filterEnd = mSurveyFilter.getEndFilterDate(getFakeTodayCalendar());
        assertTrue(mSurveyFilter.isDateBetweenDates(startDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(startDateOut, filterStart, filterEnd));
        assertTrue(mSurveyFilter.isDateBetweenDates(endDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(endDateOut, filterStart, filterEnd));
    }

    @Test
    public void testLastWeek() {
        mSurveyFilter.setLastWeek(true);
        Date startDate = getDateFromString(lastWeekStart);
        Date startDateOut = getToDateAfterDays(startDate, -1);
        Date endDate = getDateFromString(lastWeekEnd);
        Date endDateOut = getToDateAfterDays(endDate, 1);

        Date filterStart = mSurveyFilter.getStartFilterDate(getFakeTodayCalendar());
        Date filterEnd = mSurveyFilter.getEndFilterDate(getFakeTodayCalendar());
        assertTrue(mSurveyFilter.isDateBetweenDates(startDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(startDateOut, filterStart, filterEnd));
        assertTrue(mSurveyFilter.isDateBetweenDates(endDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(endDateOut, filterStart, filterEnd));
    }

    @Test
    public void testLastMonth() {
        mSurveyFilter.setLastMonth(true);
        Date startDate = getDateFromString(lastMonthStart);
        Date startDateOut = getToDateAfterDays(startDate, -1);
        Date endDate = getDateFromString(lastMonthEnd);
        Date endDateOut = getToDateAfterDays(endDate, 1);

        Date filterStart = mSurveyFilter.getStartFilterDate(getFakeTodayCalendar());
        Date filterEnd = mSurveyFilter.getEndFilterDate(getFakeTodayCalendar());
        assertTrue(mSurveyFilter.isDateBetweenDates(startDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(startDateOut, filterStart, filterEnd));
        assertTrue(mSurveyFilter.isDateBetweenDates(endDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(endDateOut, filterStart, filterEnd));
    }

    @Test
    public void testLast6Days() {
        mSurveyFilter.setLast6Days(true);
        Date startDate = getDateFromString(last6DaysStart);
        Date startDateOut = getToDateAfterDays(startDate, -1);
        Date endDate = getDateFromString(last6DaysEnd);
        Date endDateOut = getToDateAfterDays(endDate, 1);

        Date filterStart = mSurveyFilter.getStartFilterDate(getFakeTodayCalendar());
        Date filterEnd = mSurveyFilter.getEndFilterDate(getFakeTodayCalendar());
        assertTrue(mSurveyFilter.isDateBetweenDates(startDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(startDateOut, filterStart, filterEnd));
        assertTrue(mSurveyFilter.isDateBetweenDates(endDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(endDateOut, filterStart, filterEnd));
    }

    @Test
    public void testLast6Weeks() {
        mSurveyFilter.setLast6Weeks(true);
        Date startDate = getDateFromString(last6WeeksStart);
        Date startDateOut = getToDateAfterDays(startDate, -1);
        Date endDate = getDateFromString(last6WeeksEnd);
        Date endDateOut = getToDateAfterDays(endDate, 1);

        Date filterStart = mSurveyFilter.getStartFilterDate(getFakeTodayCalendar());
        Date filterEnd = mSurveyFilter.getEndFilterDate(getFakeTodayCalendar());
        assertTrue(mSurveyFilter.isDateBetweenDates(startDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(startDateOut, filterStart, filterEnd));
        assertTrue(mSurveyFilter.isDateBetweenDates(endDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(endDateOut, filterStart, filterEnd));
    }

    @Test
    public void testLast6Months() {
        mSurveyFilter.setLast6Month(true);
        Date startDate = getDateFromString(last6MonthsStart);
        Date startDateOut = getToDateAfterDays(startDate, -1);
        Date endDate = getDateFromString(last6MonthsEnd);
        Date endDateOut = getToDateAfterDays(endDate, 1);

        Date filterStart = mSurveyFilter.getStartFilterDate(getFakeTodayCalendar());
        Date filterEnd = mSurveyFilter.getEndFilterDate(getFakeTodayCalendar());
        assertTrue(mSurveyFilter.isDateBetweenDates(startDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(startDateOut, filterStart, filterEnd));
        assertTrue(mSurveyFilter.isDateBetweenDates(endDate, filterStart, filterEnd));
        assertFalse(mSurveyFilter.isDateBetweenDates(endDateOut, filterStart, filterEnd));
    }
}
