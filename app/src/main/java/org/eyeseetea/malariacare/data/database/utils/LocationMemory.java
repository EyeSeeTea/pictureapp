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
import android.location.Location;
import android.location.LocationManager;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.exception.NullContextException;

/**
 * Created by arrizabalaga on 23/09/15.
 */
public class LocationMemory {

    private static Context mContext;
    private static final String PREFIX_LATITUDE = "LAT";
    private static final String PREFIX_LONGITUDE = "LNG";
    /**
     * Singleton reference
     */
    private static LocationMemory instance;

    private LocationMemory() {
    }

    /**
     * Singleton method
     */
    public static LocationMemory getInstance() {
        if (instance == null) {
            instance = new LocationMemory();
        }
        return instance;
    }

    public void init(Context context) {
        mContext=context;
    }

    /**
     * Saves the coordinates for the given survey into internal shared preferences
     */
    public synchronized static void put(long idSurvey, Location location)
            throws NullContextException {
        if (location == null) {
            return;
        }
        SharedPreferences sharedPreferences =
                getDefaultPreferences(getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(PREFIX_LONGITUDE + idSurvey, (float) location.getLongitude());
        editor.putFloat(PREFIX_LATITUDE + idSurvey, (float) location.getLatitude());
        editor.commit();
    }

    /**
     * Gets the coordinates for a given survey id from the shared preferences
     *
     * @return A Location if it is stored in preferences, null otherwise
     */
    public static Location get(long idSurvey) throws NullContextException {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        System.out.println("gps get "+ getContext().getPackageName()+"_preferences");
        SharedPreferences sharedPreferences =
                getDefaultPreferences(Context.MODE_PRIVATE);

        float longitude = sharedPreferences.getFloat(PREFIX_LONGITUDE + idSurvey, 0f);
        float latitude = sharedPreferences.getFloat(PREFIX_LATITUDE + idSurvey, 0f);

        //No coordinates were stored for the given survey
        if (longitude == 0 && latitude == 0) {
            location.setLongitude(Double.parseDouble(getContext().getString(
                    R.string.GPS_LONGITUDE_DEFAULT)));
            location.setLatitude(Double.parseDouble(getContext().getString(
                    R.string.GPS_LATITUDE_DEFAULT)));
            return location;
        }

        //Found
        location.setLongitude(longitude);
        location.setLatitude(latitude);
        return location;
    }

    /**
     * Gets the app context
     */
    public static Context getContext() throws NullContextException {
        if(mContext==null){
            throw new NullContextException();
        }else{
            return mContext;
        }
    }

    private static SharedPreferences getDefaultPreferences(int modePrivate)
            throws NullContextException {
        return getContext().getSharedPreferences(getContext().getPackageName() + "_preferences",
                modePrivate);
    }
}
