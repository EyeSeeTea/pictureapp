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
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.eyeseetea.malariacare.views;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.squareup.okhttp.Response;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.network.PushClient;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * The EditTextPreference has not the funcionality of autocomplete.
 * Is neccesary to overwrite the methods OnBindDialogView and onDialogClosed
 */
public class AutoCompleteEditTextPreference extends EditTextPreference {


    private AutoCompleteTextView mEditText = null;
    public AutoCompleteEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEditText = new AutoCompleteTextView(context, attrs);
        mEditText.setThreshold(0);

        // Gets autocomplete values for 'org_unit' key preference
        if (getKey().equals(context.getString(R.string.org_unit))) {

            ArrayList<String> opcionesGet = new ArrayList<String>();
            String[]  orgUnits= null;
            try {
                orgUnits = new GetOrgUnitsAsync().execute(opcionesGet).get();
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

            EditTextPreference org_name = this;
            PushClient.setUnbanAndNewOrgName();
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        AutoCompleteTextView editText = mEditText;
        editText.setText(getText());

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
                setText(value);
                try {
                    boolean orgUnits = new CheckCodeAsync().execute(value).get();
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

        @Override
        protected void onPreExecute() {
        }

        protected String[] doInBackground(ArrayList<String>... passing) {
            String[] result = null;
            try {
                PushClient pushClient=new PushClient((Activity)getContext());
                result = pushClient.pullOrgUnitsCodes();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result; //return result
        }

        protected void onPostExecute(Response result) {
        }

    }

    class CheckCodeAsync extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
        }

        protected Boolean doInBackground(String... param) {
            boolean result = false;

            String orgUnit = param[0];
            try {
                PushClient pushClient=new PushClient((Activity)getContext());
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