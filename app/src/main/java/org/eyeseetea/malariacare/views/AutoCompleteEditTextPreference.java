package org.eyeseetea.malariacare.views;


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
import org.eyeseetea.malariacare.R;

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
            ArrayAdapter<String> adapter = new ArrayAdapter(this.getContext(),
                    android.R.layout.simple_dropdown_item_1line,getOrgantiationUnitOptions());
            mEditText.setAdapter(adapter);
        }
    }

    //Get from the server the organitation unit options
    private String[] getOrgantiationUnitOptions() {
        //here we will call the server and load the options
        String[] organitationUnits = new String[]{
                "Belgium", "France", "Italy", "Germany", "Spain"
        };
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