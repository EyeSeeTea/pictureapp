package org.eyeseetea.malariacare.views;


import android.app.Activity;
import android.content.Context;
import android.preference.EditTextPreference;
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
import com.squareup.okhttp.Response;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.network.GetResponse;
import org.eyeseetea.malariacare.network.PushClient;
import org.eyeseetea.malariacare.network.PushResult;
import org.eyeseetea.malariacare.network.UnsafeOkHttpsClientFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Proxy;

/**
 * The EditTextPreference has not the funcionality of autocomplete.
 * Is neccesary to overwrite the methods OnBindDialogView and onDialogClosed
 */
public class AutoCompleteEditTextPreference extends EditTextPreference {


    private AutoCompleteTextView mEditText = null;
    private String[] values;
    public AutoCompleteEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mEditText = new AutoCompleteTextView(context, attrs);
        mEditText.setThreshold(0);

        // Gets autocomplete values for 'org_unit' key preference
        if (getKey().equals(context.getString(R.string.org_unit))) {
                    values= getOrgantiationUnitOptions();
            if(values==null)
                values=new String[]{"h"};
            ArrayAdapter<String> adapter = new ArrayAdapter(this.getContext(),
                    android.R.layout.simple_dropdown_item_1line,values);
            mEditText.setAdapter(adapter);
        }
    }

    //Get from the server the organitation unit options
    private String[] getOrgantiationUnitOptions() {
        //https://malariacare.psi.org/api/programs/IrppF3qERB7.json?fields=organisationUnits

        GetResponse getResponse=new GetResponse((Activity)this.getContext());
        String[] organitationUnits = new String[0];
        try {
            organitationUnits = getResponse.getOrganization_units();
        } catch (Exception e) {
            e.printStackTrace();
            organitationUnits=  new String[]{
                    "KH_Cambodia"
            };
        }
        return organitationUnits;
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
}