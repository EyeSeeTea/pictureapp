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

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.usecase.DateFilter;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.views.FontUtils;
import org.eyeseetea.sdk.presentation.styles.FontStyle;

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
    private FontStyle fontStyle;
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

    private boolean userAccept;

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
        fontStyle = initFontStyle();
        showNumDen = initShowNumDen();
        orgUnit = initOrgUnit();
        dhisURL = initDhisURL();
        languageCode = initLanguageCode();
        Log.d(TAG, "reloadPreferences: "
                + " orgUnit:" + orgUnit
                + " |dhisURL:" + dhisURL
                + " |fontStyle:" + fontStyle.getTitle()
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
    public String initOrgUnit() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                instance.getContext());
        return sharedPreferences.getString(instance.getContext().getString(R.string.org_unit), "");
    }

    /**
     * Returns 'DhisURL' from sharedPreferences
     */
    private String initDhisURL() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                instance.getContext());
        return sharedPreferences.getString(instance.getContext().getString(R.string.dhis_url),
                instance.getContext().getString(R.string.DHIS_DEFAULT_SERVER));
    }

    private FontStyle initFontStyle() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                instance.getContext());

        if (sharedPreferences.getBoolean(instance.getContext().getString(R.string.customize_fonts),
                false)) {
            String fontStyleId = sharedPreferences.getString(instance.getContext().getString(R.string.font_sizes),
                    String.valueOf(FontStyle.Medium.getResId()));

            for (FontStyle fontStyle:FontStyle.values()) {
                if (fontStyle.getResId() == Integer.valueOf(fontStyleId))
                    return fontStyle;
            }
        }

        return FontStyle.Medium;
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

    public FontStyle getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(FontStyle fontStyle) {
        this.fontStyle = fontStyle;
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

    public boolean getMetaDataDownload() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                instance.getContext());
        return sharedPreferences.getBoolean(
                instance.getContext().getString(R.string.meta_data_download), true);
    }

    public void setMetaDataDownload(Boolean value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = sharedPref.edit(); // Get preference in editor mode
        prefEditor.putBoolean(instance.getContext().getString(R.string.meta_data_download),
                value); // set your default value here (could be empty as well)
        prefEditor.commit(); // finally save changes
    }

    public boolean getPullDataAfterMetadata() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                instance.getContext());
        return sharedPreferences.getBoolean(
                instance.getContext().getString(R.string.pull_data_after_metadata), false);
    }

    public void setPullDataAfterMetadata(Boolean value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = sharedPref.edit(); // Get preference in editor mode
        prefEditor.putBoolean(instance.getContext().getString(R.string.pull_data_after_metadata),
                value); // set your default value here (could be empty as well)
        prefEditor.commit(); // finally save changes
    }
    public boolean getDataFilteredByOrgUnit() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                instance.getContext());
        return sharedPreferences.getBoolean(
                instance.getContext().getString(R.string.data_filtered_by_preference_org_unit), true);
    }

    public void setDataFilteredByOrgUnit(Boolean value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefEditor = sharedPref.edit(); // Get preference in editor mode
        prefEditor.putBoolean(instance.getContext().getString(R.string.data_filtered_by_preference_org_unit),
                value); // set your default value here (could be empty as well)
        prefEditor.commit(); // finally save changes
    }
    public void onCreateActivityPreferences(Resources resources, Resources.Theme theme) {
        if(BuildConfig.translations) {
            loadsLanguageInActivity();
        }
        if (theme != null) {
            FontUtils.applyFontStyleByPreference(resources, theme);
        }
    }

    public void loadsLanguageInActivity() {
        if (languageCode.equals("")) {
            Locale locale = Resources.getSystem().getConfiguration().locale;
            setLocale(locale.getLanguage());
            return;
        }else {
            setLocale(languageCode);
        }
    }

    private void setLocale(String languageCode) {
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
        Log.d(TAG, "change set push in progress to "+ inProgress);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.push_in_progress), inProgress);
        editor.commit();
    }

    public Date getDateStarDateLimitFilter() {
        DateFilter dateFilter = new DateFilter();

        String dateLimit = getDataLimitedByDate();
        if (dateLimit.isEmpty()) {
            return null;
        }
        if (dateLimit.equals(getContext().getString(R.string.last_6_days))) {
            dateFilter.setLast6Days(true);
        } else if (dateLimit.equals(getContext().getString(R.string.last_6_weeks))) {
            dateFilter.setLast6Weeks(true);
        } else if (dateLimit.equals(getContext().getString(R.string.last_6_months))) {
            dateFilter.setLast6Month(true);
        }

        Calendar calendar = Calendar.getInstance();
        Date date = dateFilter.getStartFilterDate(calendar);
        return date;
    }

    public boolean downloadDataFilter() {
        String downloadData = getDataLimitedByDate();
        if (downloadData.equals(getContext().getString(R.string.no_data))) {
            return false;
        }
        return true;
    }

    public boolean isUserAccept() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        userAccept = sharedPreferences.getBoolean(
                context.getResources().getString(R.string.user_accept_key),
                false);
        return userAccept;
    }

    public boolean setUserAccept(boolean isAccepted) {
        this.userAccept = isAccepted;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getResources().getString(R.string.user_accept_key), isAccepted);
        editor.commit();
        return userAccept;
    }
    public boolean downloadMetaData () {
        return getMetaDataDownload();
    }

    public void setDataLimitedByPreferenceOrgUnit(boolean value) {
        setDataFilteredByOrgUnit(value);
    }
}
