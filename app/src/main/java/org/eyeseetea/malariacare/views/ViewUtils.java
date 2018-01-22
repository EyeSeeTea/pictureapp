package org.eyeseetea.malariacare.views;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;

public class ViewUtils {

    public static void toggleVisibility(View view) {
        int visibility = View.VISIBLE;
        if (view.getVisibility() == View.VISIBLE) {
            visibility = View.GONE;
        }
        view.setVisibility(visibility);
    }

    public static void toggleText(@NonNull TextView textView, @StringRes int idFirstText,
            @StringRes int idSecondText) {

        Context context = textView.getContext();
        String firstText = context.getString(idFirstText);
        String actualText = textView.getText().toString();

        if (actualText.equals(firstText)) {
            textView.setText(idSecondText);
        } else {
            textView.setText(idFirstText);
        }
    }
}
