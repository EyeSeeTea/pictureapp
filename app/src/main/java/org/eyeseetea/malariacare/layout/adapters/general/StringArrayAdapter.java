package org.eyeseetea.malariacare.layout.adapters.general;

import android.content.Context;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.List;

public class StringArrayAdapter extends AddlArrayAdapter<String> {

    public StringArrayAdapter(Context context, List<String> stringList) {
        super(context, stringList);
    }

    @Override
    public void drawText(CustomTextView customTextView, String word) {
        customTextView.setText(capitalizeString(word));
    }

    public String capitalizeString(String word){
        String firstLetter= word.substring(0,1).toUpperCase();
        return  firstLetter+word.substring(1,word.length());
    }

}