package org.eyeseetea.malariacare.layout.adapters.general;

import android.content.Context;

import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.List;

public class StringArrayAdapter extends AddlArrayAdapter<String> {

    public StringArrayAdapter(Context context, List<String> stringList) {
        super(context, stringList);
    }

    @Override
    public void drawText(CustomTextView customTextView, String string) {
        customTextView.setText(string);
    }

}