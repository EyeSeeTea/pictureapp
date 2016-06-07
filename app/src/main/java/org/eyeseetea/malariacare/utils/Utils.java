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

package org.eyeseetea.malariacare.utils;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Header;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Tab;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {

    static final int numberOfDecimals = 0; // Number of decimals outputs will have

    public static String round(float base, int decimalPlace){
        BigDecimal bd = new BigDecimal(Float.toString(base));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        if (decimalPlace == 0) return Integer.toString((int) bd.floatValue());
        return Float.toString(bd.floatValue());
    }

    public static String round(float base){
        return round(base, Utils.numberOfDecimals);
    }

    public static List<Object> convertTabToArray(Tab tab) {
        List<Object> result = new ArrayList<Object>();

        for (Header header : tab.getHeaders()) {
            result.add(header);
            for (Question question : header.getQuestions())
                result.add(question);

        }
        return result;
    }

    public static List<Object> convertTabToArrayCustom(Tab tab) {
        List<Object> result = new ArrayList<Object>();

        for (Header header : tab.getHeaders()) {
            result.add(header);
            for (Question question : header.getQuestions()) {
                if (question.hasChildren())
                    result.add(question);
            }
        }

        return result;
    }

    public static String getCommitHash(Context context){
        String stringCommit;
        //Check if lastcommit.txt file exist, and if not exist show as unavailable.
        int layoutId = context.getResources().getIdentifier("lastcommit", "raw", context.getPackageName());
        if (layoutId == 0){
            stringCommit=context.getString(R.string.unavailable);
        } else {
            InputStream commit = context.getResources().openRawResource( layoutId);
            stringCommit= Utils.convertFromInputStreamToString(commit).toString();
        }
        return stringCommit;
    }

    public static StringBuilder convertFromInputStreamToString(InputStream inputStream){
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = r.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder;
    }
    /**
     * Get a JSONArray and returns a String array from a key value()
     * @param value is the key in the first level.
     * @param json is JSONArray
     * @throws Exception
     */
    public static String[] jsonArrayToStringArray(JSONArray json,String value) {
        int size=0;
        for (int i = 0; i < json.length(); ++i) {
            JSONObject row = null;
            try {
                row = json.getJSONObject(i);
                if(row.getString(value)!=null)
                    size++;
            } catch (JSONException e) {
            }
        }
        int position=0;
        String[] strings=new String[size];
        for (int i = 0; i < json.length(); ++i) {
            JSONObject row = null;
            try {
                row = json.getJSONObject(i);
                if(row.getString(value)!=null)
                    strings[position++] = row.getString(value);
            } catch (JSONException e) {
            }
        }
        return strings;
    }
    /**
     * returns the system data and the event data difference in hours
     * @param limit is the time in hours
     * @param date is the Date to compare with system Date
     * @return if the difference is up than the time in hours
     * @throws Exception
     */
    public static boolean isDateOverLimit(Calendar date,int limit) {
        Calendar sysDate = Calendar.getInstance();
        sysDate.setTime(new Date());
        if(differenceInHours((Date) sysDate.getTime(), (Date) date.getTime())<limit){
            return false;
        }
        else
            return true;
    }
    /**
     * returns the system data and the event data difference in hours
     * @param limit is the time in hours
     * @param surveyDate is the Date to compare with nextDate
     * @param nextDate is the Date to compare with surveyDate
     * @return if the difference is up than the time in hours
     * @throws Exception
     */
    public static boolean isDateOverLimit(Calendar surveyDate,Calendar nextDate,int limit) {
        Calendar sysDate = Calendar.getInstance();
        sysDate.setTime(new Date());
        int difference=differenceInHours((Date) nextDate.getTime(), (Date) surveyDate.getTime());
        if(difference>=0 && difference<limit){
            return false;
        }
        else
            return true;
    }

    //Check if the provided date is under the system data.
    public static boolean isDateOverSystemDate(Calendar closedDate) {
        if(closedDate!=null) {
            Calendar sysDate = Calendar.getInstance();
            sysDate.setTime(new Date());
            if (sysDate.after(closedDate)) {
                return false;
            }
        }
        return true;
    }

    public static int differenceInHours(Date higherData, Date minisData) {
        long differenceInMs = higherData.getTime() - minisData.getTime();
        long hours = differenceInMs / (1000 * 60 * 60);
        return (int) hours;
    }

    public static Calendar parseStringToCalendar(String datestring){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            calendar.setTime(format.parse(datestring));// all done
        } catch (ParseException e) {
            calendar=null;
            e.printStackTrace();
        }
        return calendar;
    }

    public static String getClosingDateString(String format){
        Calendar sysDate = Calendar.getInstance();
        sysDate.setTime(new Date());
        sysDate.set(Calendar.HOUR, sysDate.get(Calendar.HOUR) - 24);
        SimpleDateFormat formated = new SimpleDateFormat(format);
        String dateFormatted = formated.format(sysDate.getTime());
        return dateFormatted;
    }

    public static Timestamp getClosingDateTimestamp(String format){
        Calendar sysDate = Calendar.getInstance();
        sysDate.setTime(new Date());
        sysDate.set(Calendar.HOUR, sysDate.get(Calendar.HOUR) - 24);
        Timestamp timestamp=new Timestamp(sysDate.getTime().getTime());
        return timestamp;
    }
    public static String geTodayDataString(String format){
        Calendar sysDate = Calendar.getInstance();
        sysDate.setTime(new Date());
        SimpleDateFormat formated = new SimpleDateFormat(format);
        String dateFormatted = formated.format(sysDate.getTime());
        return dateFormatted;
    }
    public static Calendar DateToCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
