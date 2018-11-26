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

import static org.eyeseetea.malariacare.data.database.model.TranslationDB.getLocalizedStringFromDB;
import static org.eyeseetea.malariacare.data.database.model.TranslationDB.wasTranslationFound;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.DisplayMetrics;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utils {

    static final int numberOfDecimals = 0; // Number of decimals outputs will have

    public static String round(float base, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(base));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        if (decimalPlace == 0) return Integer.toString((int) bd.floatValue());
        return Float.toString(bd.floatValue());
    }

    public static String round(float base) {
        return round(base, Utils.numberOfDecimals);
    }

    private static String getUserLanguage(@NonNull Context context) {
        String activeLocale = PreferencesState.getInstance().getLanguageCode();

        if (activeLocale != null || activeLocale.isEmpty()) {
            activeLocale = context.getResources().getConfiguration().locale.getLanguage();
        }
        return activeLocale;
    }

    @StringRes
    private static int getStringResByKey(@NonNull String key, @NonNull Context context) {
        return context.getResources().getIdentifier(key, "string",
                context.getPackageName());
    }


    @NonNull
    public static String getInternationalizedString(@NonNull String key) {
        Context context = PreferencesState.getInstance().getContext();
        String defaultLanguage = context.getString(R.string.default_language);
        String language = getUserLanguage(context);
        String translation = getLocalizedStringFromDB(key, language, defaultLanguage);

        if (key == null) {
            return "";
        }

        if (BuildConfig.downloadLanguagesFromServer &&
                wasTranslationFound(translation)) {
            return translation;
        } else {
            return findStringFromAndroidResource(context, key, language);
        }
    }

    @NonNull
    public static String getInternationalizedString(@NonNull String key,Context context) {
        String defaultLanguage = context.getString(R.string.default_language);
        String language = getUserLanguage(context);
        String translation = getLocalizedStringFromDB(key, language, defaultLanguage);

        if (key == null) {
            return "";
        }

        if (BuildConfig.downloadLanguagesFromServer &&
                wasTranslationFound(translation)) {
            return translation;
        } else {
            return findStringFromAndroidResource(context, key, language);
        }
    }

    public static String getUserLanguageOrDefault(Context context) {
        String language = getUserLanguage(context);

        if (language != null && !language.isEmpty()) {
            return language;
        } else {
            return context.getString(R.string.default_language);
        }
    }

    @NonNull
    private static String findStringFromAndroidResource(Context context, @NonNull String key,
            String languageCode) {

        int identifier = getStringResByKey(key, context);
        String translation = "";

        //if the id is 0 it not exist.
        if (identifier != 0) {
            try {
                translation = getLocateString(context, languageCode, identifier);
            } catch (Resources.NotFoundException notFoundException) {
                translation = key;
            }
        } else {
            translation = key;
        }
        return translation;
    }

    @NonNull
    private static String getLocateString(Context context, String locate, @StringRes int stringId) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Resources r = context.getResources();
        Configuration c = r.getConfiguration();
        c.locale = new Locale(locate);
        Resources res = new Resources(context.getAssets(), metrics, c);
        return res.getString(stringId);
    }

    public static String getCommitHash(Context context) {
        String stringCommit;
        //Check if lastcommit.txt file exist, and if not exist show as unavailable.
        int layoutId = context.getResources().getIdentifier("lastcommit", "raw",
                context.getPackageName());
        if (layoutId == 0) {
            stringCommit = context.getString(R.string.unavailable);
        } else {
            InputStream commit = context.getResources().openRawResource(layoutId);
            stringCommit = Utils.convertFromInputStreamToString(commit).toString();
        }
        return stringCommit;
    }

    public static StringBuilder convertFromInputStreamToString(InputStream inputStream) {
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
     * returns the system data and the event data difference in hours
     *
     * @param limit is the time in hours
     * @param date  is the Date to compare with system Date
     * @return if the difference is up than the time in hours
     */
    public static boolean isDateOverLimit(Calendar date, int limit) {
        Calendar sysDate = Calendar.getInstance();
        sysDate.setTime(new Date());
        if (differenceInHours(sysDate.getTime(), date.getTime()) < limit) {
            return false;
        } else {
            return true;
        }
    }

    //Check if the provided date is under the system data.
    public static boolean isDateOverSystemDate(Calendar closedDate) {
        if (closedDate != null) {
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

    public static Calendar parseStringToCalendar(String datestring) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            calendar.setTime(format.parse(datestring));// all done
        } catch (ParseException e) {
            calendar = null;
            e.printStackTrace();
        }
        return calendar;
    }

    public static Date parseStringToDate(String datestring) {
        return parseStringToCalendar(datestring).getTime();
    }

    public static String parseDateToString(Date date, String dateFormat) {
        DateFormat df = new SimpleDateFormat(dateFormat);
        return df.format(date);

    }

    public static String parseDateToString(Date date, String dateFormat, TimeZone timeZone) {
        DateFormat df = new SimpleDateFormat(dateFormat);
        df.setTimeZone(timeZone);
        return df.format(date);

    }

    public static Calendar parseStringToCalendar(String datestring, String dateFormat) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.US);
        try {
            calendar.setTime(format.parse(datestring));// all done
        } catch (ParseException e) {
            calendar = null;
            e.printStackTrace();
        }
        return calendar;
    }

    public static String getStringFromCalendarWithFormat(Calendar calendar, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.US);
        return format.format(calendar.getTime());
    }


    public static String getClosingDateString(String format) {
        Calendar sysDate = Calendar.getInstance();
        sysDate.setTime(new Date());
        sysDate.set(Calendar.HOUR, sysDate.get(Calendar.HOUR) - 24);
        SimpleDateFormat formated = new SimpleDateFormat(format);
        String dateFormatted = formated.format(sysDate.getTime());
        return dateFormatted;
    }

    public static Timestamp getClosingDateTimestamp(String format) {
        Calendar sysDate = Calendar.getInstance();
        sysDate.setTime(new Date());
        sysDate.set(Calendar.HOUR, sysDate.get(Calendar.HOUR) - 24);
        Timestamp timestamp = new Timestamp(sysDate.getTime().getTime());
        return timestamp;
    }

    public static String geTodayDataString(String format) {
        Calendar sysDate = Calendar.getInstance();
        sysDate.setTime(new Date());
        SimpleDateFormat formated = new SimpleDateFormat(format);
        String dateFormatted = formated.format(sysDate.getTime());
        return dateFormatted;
    }

    /**
     * Method to get the date of today with hour at 0
     */
    public static Date getTodayDate() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        return today.getTime();
    }

    public static Calendar DateToCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    /**
     * Method to get if the endDate is grater or equal than the startDate
     *
     * @param starDate The start date to compare with.
     * @param endDate  The date tha has to be greater or equals to the start date.
     * @return True if is greater or equals false if not.
     */
    public static boolean dateGreaterOrEqualsThanDate(Date starDate, Date endDate) {
        return starDate.equals(endDate) || starDate.before(endDate);
    }
}
