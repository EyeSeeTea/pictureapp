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

package org.eyeseetea.malariacare.database.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.layout.adapters.dashboard.IDashboardAdapter;
import org.eyeseetea.malariacare.phonemetadata.PhoneMetaData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * An application scoped object that stores transversal information:
 *  -User
 *  -Survey
 *  -..
 */
public class Session {

    private final static String TAG=".Session";

    /**
     * The current selected survey
     */
    private static Survey survey;
    /**
     * The current user
     */
    private static User user;

    /**
     * The current location
     */
    private static Location location;

    /**
     * The current phone metadata
     */
    private static PhoneMetaData phoneMetaData;

    /**
     * Lock to protect the inclusion or extraction of any value in a concurrent way
     */
    final public static ReentrantReadWriteLock valuesLock = new ReentrantReadWriteLock();

    /**
     * Map that holds non serializable results from services
     */
    private static Map<String,Object> serviceValues=new HashMap<>();

    //FIXME Probably no longer required
    private static IDashboardAdapter adapterUncompleted, adapterCompleted;

    public static Survey getSurvey() {
        return survey;
    }

    public static synchronized void setSurvey(Survey survey) {
        Session.survey = survey;
    }

    public static User getUser() {
        return user;
    }

    public static synchronized void setUser(User user) {
        Session.user = user;
    }

    public static IDashboardAdapter getAdapterUncompleted() {
        return adapterUncompleted;
    }

    public static synchronized void setAdapterUncompleted(IDashboardAdapter adapterUncompleted) {
        Session.adapterUncompleted = adapterUncompleted;
    }

    public static IDashboardAdapter getAdapterCompleted() {
        return adapterCompleted;
    }

    public static synchronized void setAdapterCompleted(IDashboardAdapter adapterCompleted) {
        Session.adapterCompleted = adapterCompleted;
    }

    public static synchronized void setFullOfUnsent(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.fullOfUnsent), true);
        editor.commit();
    }

    public static synchronized void setNotFullOfUnsent(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.fullOfUnsent), false);
        editor.commit();
    }

    public static boolean isNotFullOfUnsent(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return !sharedPreferences.getBoolean(context.getResources().getString(R.string.fullOfUnsent),false);
    }

    /**
     * Closes the current session when the user logs out
     */
    public static void logout(){
        List<Survey> surveys = Survey.getAllUnsentSurveys();
        for (Survey survey : surveys) {
            survey.delete();
        }
        Session.getUser().delete();
        Session.setUser(null);
        Session.setSurvey(null);
        Session.setAdapterUncompleted(null);
        Session.serviceValues.clear();
    }

    /**
     * Puts a pair key/value into a shared map.
     * Used to share values that are not serializable and thus cannot be put into an intent (domains and so).
     * @param key
     * @param value
     */
    public static void putServiceValue(String key, Object value){
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
     * @param key
     * @return
     */
    public static Object popServiceValue(String key){
        return serviceValues.get(key);
//        return serviceValues.remove(key);
    }

    /**
     * Clears the service values in memory.
     * Used for clean testing.
     */
    public static void clearServiceValues(){

        serviceValues.clear();
    }

    public static Location getLocation() {
        return location;
    }

    public static synchronized void setLocation(Location location) {
        Session.location = location;
    }

    public static PhoneMetaData getPhoneMetaData(){return phoneMetaData;}

    public static synchronized void setPhoneMetaData(PhoneMetaData phoneMetaData) {
        Session.phoneMetaData = phoneMetaData;
    }

}
