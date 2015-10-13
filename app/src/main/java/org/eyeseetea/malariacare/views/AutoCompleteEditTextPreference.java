package org.eyeseetea.malariacare.views;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.network.GetResponse;
import org.eyeseetea.malariacare.network.PushClient;
import org.eyeseetea.malariacare.network.PushResult;
import org.eyeseetea.malariacare.network.UnsafeOkHttpsClientFactory;
import org.eyeseetea.malariacare.services.SurveyService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;
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
                orgUnits = new getBackground().execute(opcionesGet).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            //If the call to the server fails, suggest the default code
            if(orgUnits==null)
                orgUnits=new String[]{"KH_Cambodia"};
            ArrayAdapter<String> adapter = new ArrayAdapter(this.getContext(),
                    android.R.layout.simple_dropdown_item_1line,orgUnits);
            mEditText.setAdapter(adapter);
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
            }
        }
    }

    class getBackground extends AsyncTask<ArrayList<String>, Void, String[]> {

        @Override
        protected void onPreExecute() {
        }

        protected String[] doInBackground(ArrayList<String>... passing) {
            String[] result = null;
            try {
                PushClient pushClient=new PushClient(null);
                result = pushClient.pullOrgUnitsCodes();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result; //return result
        }

        protected void onPostExecute(Response result) {
        }

    }
}