package org.eyeseetea.malariacare.views;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.annotation.NotNull;

import org.eyeseetea.malariacare.utils.Utils;

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
            textView.setText(Utils.getInternationalizedString(idSecondText,context));
        } else {
            textView.setText(Utils.getInternationalizedString(idFirstText,context));
        }
    }

    public static void showToast(@StringRes int titleResource, @NotNull Context context) {
        final String title = Utils.getInternationalizedString(titleResource, context);
        Toast.makeText(context, title, Toast.LENGTH_LONG).show();
    }

    public static boolean isThereAnAppThatCanHandleThis(@NotNull Intent intent,
            @NotNull Context context) {
        PackageManager packageManager = context.getPackageManager();
        return intent.resolveActivity(packageManager) != null;
    }

}
