package org.eyeseetea.malariacare.domain.usecase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFilter {

    boolean all = true;
    boolean thisWeek;
    boolean thisMonth;
    boolean lastWeek;
    boolean lastMonth;
    boolean last6Days;
    boolean last6Weeks;
    boolean last6Month;
    boolean today;

    public boolean isAll() {
        return all;
    }

    public boolean isThisWeek() {
        return thisWeek;
    }

    public void setThisWeek(boolean thisWeek) {
        all = false;
        this.thisWeek = thisWeek;
    }

    public boolean isThisMonth() {
        return thisMonth;
    }

    public void setThisMonth(boolean thisMonth) {
        all = false;
        this.thisMonth = thisMonth;
    }

    public boolean isLastWeek() {
        return lastWeek;
    }

    public void setLastWeek(boolean last_week) {
        all = false;
        this.lastWeek = last_week;
    }

    public boolean isLastMonth() {
        return lastMonth;
    }

    public void setLastMonth(boolean lastMonth) {
        all = false;
        this.lastMonth = lastMonth;
    }

    public boolean isToday() {
        return today;
    }

    public void setToday(boolean today) {
        all = false;
        this.today=today;
    }

    //This filter include the current day
    public boolean isLast6Days() {
        return last6Days;
    }

    public void setLast6Days(boolean last6Days) {
        all = false;
        this.last6Days = last6Days;
    }

    //This filter include the current week
    public boolean isLast6Weeks() {
        return last6Weeks;
    }

    public void setLast6Weeks(boolean last6Weeks) {
        all = false;
        this.last6Weeks = last6Weeks;
    }
    //This filter ignore the current month
    public boolean isLast6Month() {
        return last6Month;
    }

    public void setLast6Month(boolean last6Month) {
        all = false;
        this.last6Month = last6Month;
    }

    public Date getStartFilterDate(Calendar calendar) {
        if (isToday()) {
            clearTime(calendar);
        } else if (isLast6Days()) {
            calendar.add(Calendar.DAY_OF_YEAR, -5);
        } else if (isLast6Weeks()) {
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            calendar.add(Calendar.WEEK_OF_YEAR, -5);
        } else if (isLast6Month()) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MONTH, -5);
        } else if (isThisWeek()) {
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        } else if (isThisMonth()) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        } else if (isLastWeek()) {
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        } else if (isLastMonth()) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.MONTH, -1);
        }
        clearTime(calendar);
        return calendar.getTime();
    }

    public Date getEndFilterDate(Calendar calendar) {
        if (isLastWeek()) {
            //the end of the last week
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
            calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
            calendar.add(Calendar.DAY_OF_YEAR, 6);
        } else if (isLastMonth()) {
            //the end of the last month
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
        } else if  (isLast6Month() || isLast6Weeks() || isLast6Days() || isToday() || isThisMonth() || isThisWeek()){
            //this moment
            return calendar.getTime();
        }
        completeDayTime(calendar);
        return calendar.getTime();
    }

    public boolean isDateBetweenDates(Date dateInTheMiddle, Date startDate, Date endDate) {
        System.out.println(
                "startDate: " + getHumanReadableDate(startDate) + " middleDate: " + getHumanReadableDate(
                        dateInTheMiddle) + " endDate " + getHumanReadableDate(endDate));
        return dateInTheMiddle.getTime() >= startDate.getTime()
                && dateInTheMiddle.getTime() <= endDate.getTime();
    }

    private String getHumanReadableDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String dateAsString = sdf.format(date);
        return dateAsString;
    }

    /**
     * Wipes off time info from date
     *
     * @return A new date without time data (00:00:00:000)
     */
    private Calendar clearTime(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date;
    }
    /**
     * Wipes off time info from date
     *
     * @return A new date without time data (00:00:00:000)
     */
    private Calendar completeDayTime(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 23);
        date.set(Calendar.MINUTE, 59);
        date.set(Calendar.SECOND, 59);
        date.set(Calendar.MILLISECOND, 999);
        return date;
    }
}
