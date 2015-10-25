/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Survelliance App.
 *
 *  QIS Survelliance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Survelliance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Survelliance App.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.eyeseetea.malariacare.views;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.squareup.okhttp.Response;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.network.PushClient;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * The EditTextPreference has not the funcionality of autocomplete.
 * Is neccesary to overwrite the methods OnBindDialogView and onDialogClosed
 */
public class AutoCompleteEditTextPreference extends EditTextPreference {

    private Context context;
    private AutoCompleteTextView mEditText = null;

    public AutoCompleteEditTextPreference(final Context context, AttributeSet attrs) {
        super(context, attrs);
        mEditText = new AutoCompleteTextView(context, attrs);
        mEditText.setThreshold(0);
        this.context = context;
        // Gets autocomplete values for 'org_unit' key preference
        if (getKey().equals(context.getString(R.string.org_unit))) {
            pullOrgUnits();
        }

    }

    @Override
    public void setOnPreferenceChangeListener(OnPreferenceChangeListener onPreferenceChangeListener) {
        super.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor prefEditor = sharedPref.edit(); // Get preference in editor mode
                prefEditor.putString(getContext().getString(R.string.org_unit), newValue.toString()); // set your default value here (could be empty as well)
                prefEditor.commit(); // finally save changes
                preference.setSummary(newValue.toString());
                mEditText.setText(newValue.toString());
                PreferencesState.getInstance().reloadPreferences();
                PushClient.newOrgUnitOrServer();
                return true;
            }
        });

    }

    public void pullOrgUnits() {
        PreferencesState.getInstance().reloadPreferences();
        ArrayList<String> opcionesGet = new ArrayList<String>();
        String[]  orgUnits= null;
        try {
            GetOrgUnitsAsync getOrgUnitsAsynctask = new GetOrgUnitsAsync(context);
            orgUnits = getOrgUnitsAsynctask.execute(opcionesGet).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //If the call to the server fails, suggest the default code
        if(orgUnits==null) {
            //Fixme bad smell (this value is the same of the DHIS_DEFAULT_CODE of PushClient, but in PushClient you can´t acces to strings.xml sometimes.
            orgUnits = new String[]{"KH_Cambodia"};
        }

        ArrayAdapter<String> adapter = new ArrayAdapter(this.getContext(),
                android.R.layout.simple_dropdown_item_1line,orgUnits);
        mEditText.setAdapter(adapter);
        PushClient.newOrgUnitOrServer();
    }

    @Override
    protected void onBindDialogView(View view) {
        AutoCompleteTextView editText = mEditText;
        SharedPreferences preferences = view.getContext().getSharedPreferences("org.eyeseetea.pictureapp_preferences", view.getContext().MODE_PRIVATE);
        String key=view.getContext().getResources().getString(R.string.org_unit);
        String value=preferences.getString(key, "");
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
        if (positiveResult) {
            String value = mEditText.getText().toString();
            if (callChangeListener(value)) {
                mEditText.setText(value);
                try {

                    CheckCodeAsync checkCodeAsync = new CheckCodeAsync(mEditText.getContext());
                    boolean orgUnits = checkCodeAsync.execute(value).get();
                    if(!orgUnits)
                    {
                        try {
                            throw new ShowException(this.getContext().getString(R.string.exception_org_unit_not_valid), this.getContext());
                        } catch (ShowException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    class GetOrgUnitsAsync extends AsyncTask<ArrayList<String>, Void, String[]> {

        Context context;
        public GetOrgUnitsAsync(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
        }

        protected String[] doInBackground(ArrayList<String>... passing) {
            boolean validServer=false;
            String[] result = null;
            try {
                PushClient pushClient=new PushClient(null,context);
                validServer=pushClient.isValidServer();
                if(validServer)
                    result = pushClient.pullOrgUnitsCodes();
                else
                    result[0] = "";
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result; //return result
        }

        protected void onPostExecute(Response result) {
        }

    }

    class CheckCodeAsync extends AsyncTask<String, Void, Boolean> {

        Context context;
        public CheckCodeAsync(Context context) {
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
        }

        protected Boolean doInBackground(String... param) {
            boolean result = false;

            String orgUnit = param[0];
            try {
                PushClient pushClient=new PushClient(context);
                result = pushClient.checkOrgUnit(orgUnit);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(result)
                return Boolean.TRUE;
            else
                return Boolean.FALSE;
        }

        protected void onPostExecute(Response result) {
        }

    }

}