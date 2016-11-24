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
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Singleton that holds info related to preferences
 * Created by arrizabalaga on 26/06/15.
 */
public class PreferencesState {

    static Context context;
    private static String TAG = ".PreferencesState";
    /**
     * Singleton reference
     */
    private static PreferencesState instance;
    /**
     * Active language code;
     */
    private static String languageCode;
    /**
     * Selected scale, one between [xsmall,small,medium,large,xlarge,system]
     */
    private String scale;
    /**
     * Flag that determines if numerator/denominator are shown in scores.
     */
    private boolean showNumDen;
    /**
     * Flag that determines if the url server was changed
     */
    private boolean isNewServerUrl;
    /**
     * Specified Organization Unit
     */
    private String orgUnit;
    /**
     * Specified DHIS2 Server
     */
    private String dhisURL;
    /**
     * Map that holds the relationship between a scale and a set of dimensions
     */
    private Map<String, Map<String, Float>> scaleDimensionsMap;

    private PreferencesState() {
    }

    public static PreferencesState getInstance() {
        if (instance == null) {
            instance = new PreferencesState();
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        scaleDimensionsMap = initScaleDimensionsMap();
        reloadPreferences();
    }

    public Context getContext() {
        return context;
    }

    public void reloadPreferences() {
        scale = initScale();
        showNumDen = initShowNumDen();
        orgUnit = initOrgUnit();
        dhisURL = initDhisURL();
        languageCode = initLanguageCode();
        Log.d(TAG, "reloadPreferences: "
                + " orgUnit:" + orgUnit
                + " |dhisURL:" + dhisURL
                + " |scale:" + scale
                + " | showNumDen:" + showNumDen);
    }

    /**
     * Returns 'language code' from sharedPreferences
     */
    private String initLanguageCode() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                instance.getContext());
        return sharedPreferences.getString(instance.getContext().getString(R.string.language_code),
                "");
    }

    /**
     * Returns 'org_unit' from sharedPreferences
     */
    private String initOrgUnit() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                instance.getContext());
        return sharedPreferences.getString(instance.getContext().getString(R.string.org_unit), "");
    }

    /**
     * Returns 'org_unit' from sharedPreferences
     */
    private String initDhisURL() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                instance.getContext());
        return sharedPreferences.getString(instance.getContext().getString(R.string.dhis_url),
                instance.getContext().getString(R.string.DHIS_DEFAULT_SERVER));
    }

    /**
     * Inits scale according to preferences
     */
    private String initScale() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                instance.getContext());
        if (sharedPreferences.getBoolean(instance.getContext().getString(R.string.customize_fonts),
                false)) {
            return sharedPreferences.getString(instance.getContext().getString(R.string.font_sizes),
                    Constants.FONTS_SYSTEM);
        }

        return Constants.FONTS_SYSTEM;
    }

    /**
     * Inits flag according to preferences
     */
    private boolean initShowNumDen() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                instance.getContext());
        return sharedPreferences.getBoolean(instance.getContext().getString(R.string.show_num_dems),
                false);
    }

    /**
     * Inits maps of dimensions
     */
    private Map<String, Map<String, Float>> initScaleDimensionsMap() {
        Map<String, Float> xsmall = new HashMap<>();
        String xsmallKey = instance.getContext().getString(R.string.font_size_level0),
                smallKey = context.getString(R.string.font_size_level1),
                mediumKey = context.getString(R.string.font_size_level2),
                largeKey = context.getString(R.string.font_size_level3),
                xlargeKey = context.getString(R.string.font_size_level4);

        xsmall.put(xsmallKey, context.getResources().getDimension(R.dimen.xsmall_xsmall_text_size));
        xsmall.put(smallKey, context.getResources().getDimension(R.dimen.xsmall_small_text_size));
        xsmall.put(mediumKey, context.getResources().getDimension(R.dimen.xsmall_medium_text_size));
        xsmall.put(largeKey, context.getResources().getDimension(R.dimen.xsmall_large_text_size));
        xsmall.put(xlargeKey, context.getResources().getDimension(R.dimen.xsmall_xlarge_text_size));
        Map<String, Float> small = new HashMap<>();
        small.put(xsmallKey, context.getResources().getDimension(R.dimen.small_xsmall_text_size));
        small.put(smallKey, context.getResources().getDimension(R.dimen.small_small_text_size));
        small.put(mediumKey, context.getResources().getDimension(R.dimen.small_medium_text_size));
        small.put(largeKey, context.getResources().getDimension(R.dimen.small_large_text_size));
        small.put(xlargeKey, context.getResources().getDimension(R.dimen.small_xlarge_text_size));
        Map<String, Float> medium = new HashMap<>();
        medium.put(xsmallKey, context.getResources().getDimension(R.dimen.medium_xsmall_text_size));
        medium.put(smallKey, context.getResources().getDimension(R.dimen.medium_small_text_size));
        medium.put(mediumKey, context.getResources().getDimension(R.dimen.medium_medium_text_size));
        medium.put(largeKey, context.getResources().getDimension(R.dimen.medium_large_text_size));
        medium.put(xlargeKey, context.getResources().getDimension(R.dimen.medium_xlarge_text_size));
        Map<String, Float> large = new HashMap<>();
        large.put(xsmallKey, context.getResources().getDimension(R.dimen.large_xsmall_text_size));
        large.put(smallKey, context.getResources().getDimension(R.dimen.large_small_text_size));
        large.put(mediumKey, context.getResources().getDimension(R.dimen.large_medium_text_size));
        large.put(largeKey, context.getResources().getDimension(R.dimen.large_large_text_size));
        large.put(xlargeKey, context.getResources().getDimension(R.dimen.large_xlarge_text_size));
        Map<String, Float> xlarge = new HashMap<>();
        xlarge.put(xsmallKey, context.getResources().getDimension(R.dimen.extra_xsmall_text_size));
        xlarge.put(smallKey, context.getResources().getDimension(R.dimen.extra_small_text_size));
        xlarge.put(mediumKey, context.getResources().getDimension(R.dimen.extra_medium_text_size));
        xlarge.put(largeKey, context.getResources().getDimension(R.dimen.extra_large_text_size));
        xlarge.put(xlargeKey, context.getResources().getDimension(R.dimen.extra_xlarge_text_size));

        Map scaleDimensionsMap = new HashMap<>();
        scaleDimensionsMap.put(xsmallKey, xsmall);
        scaleDimensionsMap.put(smallKey, small);
        scaleDimensionsMap.put(mediumKey, medium);
        scaleDimensionsMap.put(largeKey, large);
        scaleDimensionsMap.put(xlargeKey, xlarge);
        return scaleDimensionsMap;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String value) {
        this.scale = value;
    }

    public boolean isShowNumDen() {
        return showNumDen;
    }

    public void setShowNumDen(boolean value) {
        this.showNumDen = value;
    }

    public boolean isNewServerUrl() {
        return isNewServerUrl;
    }

    public void setIsNewServerUrl(boolean value) {
        this.isNewServerUrl = value;
    }

    public Float getFontSize(String scale, String dimension) {
        return scaleDimensionsMap.get(scale).get(dimension);
    }

    public String getOrgUnit() {
        return orgUnit;
    }

    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }

    public String getDhisURL() {
        return dhisURL;
    }

    public void setDhisURL(String dhisURL) {
        this.dhisURL = dhisURL;
    }

    public String getLanguageCode() {
        return languageCode;
    }


    /**
     * Saves a value into a preference
     */
    public void saveStringPreference(int namePreference, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = sharedPref.edit(); // Get preference in editor mode
        prefEditor.putString(context.getResources().getString(namePreference),
                value); // set your default value here (could be empty as well)
        prefEditor.commit(); // finally save changes
    }

    public void saveStringPreference(String namePreference, String value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = sharedPref.edit(); // Get preference in editor mode
        prefEditor.putString(namePreference,
                value); // set your default value here (could be empty as well)
        prefEditor.commit(); // finally save changes
    }

    public String getDataLimitedByDate() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                instance.getContext());
        return sharedPreferences.getString(
                instance.getContext().getString(R.string.data_limited_by_date), "");
    }

    public void setDataLimitedByDate(String value) {
        saveStringPreference(R.string.data_limited_by_date, value);
    }

    public void loadsLanguageInActivity() {
        if (languageCode.equals("")) {
            return;
        }
        Resources res = context.getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(languageCode);
        res.updateConfiguration(conf, dm);
    }

    /**
     * Inits hidePlanningTab flag according to preferences
     */
    public boolean isDevelopOptionActive() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                instance.getContext());
        return sharedPreferences.getBoolean(
                instance.getContext().getString(R.string.developer_option), false);
    }
}
