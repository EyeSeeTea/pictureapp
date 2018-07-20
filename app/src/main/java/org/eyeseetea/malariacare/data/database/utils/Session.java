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

package org.eyeseetea.malariacare.data.database.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationController;
import org.eyeseetea.malariacare.phonemetadata.PhoneMetaData;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * An application scoped object that stores transversal information:
 * -User
 * -Survey
 * -..
 */
public class Session {

    /**
     * Lock to protect the inclusion or extraction of any value in a concurrent way
     */
    final public static ReentrantReadWriteLock valuesLock = new ReentrantReadWriteLock();
    private final static String TAG = ".Session";
    /**
     * The current selected malariaSurvey
     */
    private static SurveyDB sMalariaSurveyDB;
    /**
     * The current stock malariaSurvey
     */
    private static SurveyDB sStockSurveyDB;
    /**
     * The current user
     */
    private static UserDB sUserDB;
    /**
     * The current credentials
     */
    private static Credentials sCredentials;
    /**
     * The current phone metadata
     */
    private static PhoneMetaData phoneMetaData;

    private static boolean hasSurveyToComplete;
    /**
     * The exitOfSurvey
     */
    //FIXME: 09/03/2017    This variable should disappear instance of a refactor in loading of a new survey
    private static boolean isLoadingNavigationController;
    //FIXME: 09/03/2017    This variable should disappear instance of a refactor in loading of a new survey
    private static boolean shouldPressBackOnLoadSurvey;
    /**
     * The maximum total of questions in programm
     */
    private static int maxTotalQuestions;

    private static NavigationController sNavigationController;

    /**
     * Map that holds non serializable results from services
     */
    private static Map<String, Object> serviceValues = new HashMap<>();

    public static SurveyDB getMalariaSurveyDB() {
        return sMalariaSurveyDB;
    }

    public static synchronized void setMalariaSurveyDB(SurveyDB malariaSurveyDB) {
        Session.sMalariaSurveyDB = malariaSurveyDB;
    }

    public static SurveyDB getStockSurveyDB() {
        return sStockSurveyDB;
    }

    public static synchronized void setStockSurveyDB(SurveyDB stockSurveyDB) {
        Session.sStockSurveyDB = stockSurveyDB;
    }

    public static Credentials getCredentials() {
        if (sCredentials == null) {
            sCredentials = PreferencesState.getInstance().getCredentialsFromPreferences();
        }
        return sCredentials;
    }

    public static void setCredentials(Credentials credentials) {
        sCredentials = credentials;
    }

    public static UserDB getUserDB() {
        if (sUserDB == null) {
            sUserDB = UserDB.getLoggedUser();
        }
        return sUserDB;
    }

    public static synchronized void setUserDB(UserDB userDB) {
        Session.sUserDB = userDB;
    }

    public static synchronized void setFullOfUnsent(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.fullOfUnsent), true);
        editor.commit();
    }

    public static synchronized void setNotFullOfUnsent(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.fullOfUnsent), false);
        editor.commit();
    }

    public static boolean isNotFullOfUnsent(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return !sharedPreferences.getBoolean(
                context.getResources().getString(R.string.fullOfUnsent), false);
    }

    /**
     * Closes the current session when the user logs out
     */
    public static void logout() {
        Session.setUserDB(null);
        Session.setMalariaSurveyDB(null);
        Session.serviceValues.clear();
    }

    /**
     * Puts a pair key/value into a shared map.
     * Used to share values that are not serializable and thus cannot be put into an intent
     * (domains
     * and so).
     */
    public static void putServiceValue(String key, Object value) {
        valuesLock.writeLock().lock();
        try {
            Log.i(TAG, "putServiceValue(" + key + ", " + value.toString() + ")");
            serviceValues.put(key, value);
        } finally {
            valuesLock.writeLock().unlock();
        }
    }

    /**
     * Pops the value of the given key out of the map.
     */
    public static Object popServiceValue(String key) {
        return serviceValues.get(key);
    }

    /**
     * Clears the service values in memory.
     * Used for clean testing.
     */
    public static void clearServiceValues() {

        serviceValues.clear();
    }

    public static String getPhoneMetaDataValue() {
        if(phoneMetaData==null || phoneMetaData.getPhone_metaData()==null){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    PreferencesState.getInstance().getContext());
            return sharedPreferences.getString(
                    PreferencesState.getInstance().getContext().getString(R.string.phone_meta_data_key), "");
        }
        return phoneMetaData.getPhone_metaData();
    }

    public static synchronized void setPhoneMetaData(PhoneMetaData phoneMetaData) {
        PreferencesState.getInstance().saveStringPreference(R.string.phone_meta_data_key, phoneMetaData.getPhone_metaData());
        Session.phoneMetaData = phoneMetaData;
    }

    public static int getMaxTotalQuestions() {
        return maxTotalQuestions;
    }

    public static synchronized void setMaxTotalQuestions(int maxTotalQuestions) {
        Session.maxTotalQuestions = maxTotalQuestions;
    }

    public static boolean isIsLoadingNavigationController() {
        return isLoadingNavigationController;
    }

    public static void setIsLoadingNavigationController(boolean value) {
        isLoadingNavigationController = value;
    }

    public static NavigationController getNavigationController() {
        return sNavigationController;
    }

    public static void setNavigationController(NavigationController navigationController) {
        sNavigationController = navigationController;
    }

    public static boolean hasSurveyToComplete() {
        return hasSurveyToComplete;
    }

    public static void setHasSurveyToComplete(boolean hasSurveyToComplete) {
        Session.hasSurveyToComplete = hasSurveyToComplete;
    }
}
