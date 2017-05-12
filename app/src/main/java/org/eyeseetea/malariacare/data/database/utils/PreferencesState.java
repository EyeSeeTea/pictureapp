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
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.FontUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

    private PreferencesState() {
    }

    public static PreferencesState getInstance() {
        if (instance == null) {
            instance = new PreferencesState();
        }
        return instance;
    }

    /**
     * Get credentials from sharedPreferences.
     */
    public static Credentials getCredentialsFromPreferences() {
        Context context = PreferencesState.getInstance().getContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        String url = sharedPreferences.getString(context.getString(R.string.dhis_url), "");

        String username = sharedPreferences.getString(context.getString(R.string.dhis_user), "");
        String password = sharedPreferences.getString(context.getString(R.string.dhis_password),
                "");
        Credentials credentials = new Credentials(url, username, password);

        return credentials;
    }

    public void init(Context context) {
        this.context = context;
        reloadPreferences();
    }

    public Context getContext() {
        return context;
    }

    public void reloadPreferences() {
        if (context == null) {
            Log.d(TAG, "reloadPreferences: "
                    + " the context is null");
            return;
        }
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

    public String getScale() {
        if (scale == null) {
            scale = initScale();
        }
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

    public void onCreateActivityPreferences(Resources resources, Resources.Theme theme) {
        loadsLanguageInActivity();
        if (theme != null) {
            FontUtils.applyFontStyleByPreference(resources, theme);
        }
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

    public boolean isPushInProgress() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                instance.getContext());
        return sharedPreferences.getBoolean(
                instance.getContext().getString(R.string.push_in_progress), false);
    }

    public void setPushInProgress(boolean inProgress) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.push_in_progress), inProgress);
        editor.commit();
    }

    public Date getDateStarDateLimitFilter() {
        String dateLimit = getDataLimitedByDate();
        if (dateLimit.isEmpty()) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        if (dateLimit.equals(getContext().getString(R.string.last_6_days))) {
            calendar.add(Calendar.DAY_OF_YEAR, -6);
        } else if (dateLimit.equals(getContext().getString(R.string.last_6_weeks))) {
            calendar.add(Calendar.WEEK_OF_YEAR, -6);
        } else if (dateLimit.equals(getContext().getString(R.string.last_6_months))) {
            calendar.add(Calendar.MONTH, -6);
        }
        return calendar.getTime();
    }

    public boolean downloadDataFilter() {
        String downloadData = getDataLimitedByDate();
        if (downloadData.equals(getContext().getString(R.string.no_data))) {
            return false;
        }
        return true;
    }
}
