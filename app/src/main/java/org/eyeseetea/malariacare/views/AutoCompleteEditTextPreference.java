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


package org.eyeseetea.malariacare.views;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AutoCompleteTextView;

import org.eyeseetea.malariacare.ProgressActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.views.filters.AutocompleteAdapterFilter;

/**
 * The EditTextPreference has not the funcionality of autocomplete.
 * Is neccesary to overwrite the methods OnBindDialogView and onDialogClosed
 */
public class AutoCompleteEditTextPreference extends EditTextPreference {

    private static final String TAG = ".AutoPreference";
    private Context context;
    private AutoCompleteTextView mEditText = null;

    public AutoCompleteEditTextPreference(final Context context, AttributeSet attrs) {
        super(context, attrs);
        mEditText = new AutoCompleteTextView(context, attrs);
        mEditText.setThreshold(0);
        this.context = context;
    }
    SettingsActivity settingsActivity;

    private void pullData() {
        PreferencesState.getInstance().setMetaDataDownload(false);
        PreferencesState.getInstance().setPullDataAfterMetadata(true);
        PreferencesState.getInstance().setDataLimitedByPreferenceOrgUnit(true);

        settingsActivity.startActivity(new Intent(settingsActivity, ProgressActivity.class));
    }

    public void setContext(SettingsActivity settingsActivity) {
        this.settingsActivity=settingsActivity;
    }
    public void pullOrgUnits() {

        //Reload options
        String[] orgUnits;

        orgUnits = findOrgUnitsFromDB();

        AutocompleteAdapterFilter<String> adapter = new AutocompleteAdapterFilter(this.getContext(),
                android.R.layout.simple_dropdown_item_1line, orgUnits);
        mEditText.setAdapter(adapter);
    }

    private String[] findOrgUnitsFromDB() {
        return OrgUnit.listAllNames();
    }

    @Override
    protected void onBindDialogView(View view) {
        //XXX Hack to avoid having a default edittext instead of our custom autocomplete
        //super.onBindDialogView(view);

        AutoCompleteTextView editText = mEditText;
        SharedPreferences preferences = view.getContext().getSharedPreferences(
                "org.eyeseetea.surveillance_kh_preferences", view.getContext().MODE_PRIVATE);
        String key = view.getContext().getResources().getString(R.string.org_unit);
        String value = preferences.getString(key, "");
        editText.setText(value);
        ViewParent oldParent = editText.getParent();
        if (oldParent != view) {
            if (oldParent != null) {
                ((ViewGroup) oldParent).removeView(editText);
            }
            onAddEditTextToDialogView(view, editText);
        }
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                PreferencesState.getInstance().getContext());
        if (sharedPreferences.getBoolean(
                PreferencesState.getInstance().getContext().getApplicationContext().getResources
                        ().getString(
                        R.string.eula_accepted), false) && positiveResult) {
            String value = mEditText.getText().toString();

            boolean pullRequired = false;
            if(!PreferencesState.getInstance().getOrgUnit().equals(value)) {
                pullRequired=true;
            }
            PreferencesState.getInstance().saveStringPreference(R.string.org_unit,
                    value);
            PreferencesState.getInstance().reloadPreferences();

            //Super invokes changeListener
            callChangeListener(value);
            if(pullRequired){
                pullData();
            }
        }
    }
}