package org.eyeseetea.malariacare.views;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.DatePicker;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.IllegalFormatConversionException;


public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {

    private int year;
    private int month;
    private int day;
    private DateTime dateTime;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private DatePickerDialog dialog;

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Context context = getActivity();
        if (isBrokenSamsungDevice()) {
            context = new ContextWrapper(getActivity()) {

                private Resources wrappedResources;

                @Override
                public Resources getResources() {
                    Resources r = super.getResources();
                    if (wrappedResources == null) {
                        wrappedResources = new Resources(r.getAssets(), r.getDisplayMetrics(),
                                r.getConfiguration()) {
                            @NonNull
                            @Override
                            public String getString(int id, Object... formatArgs)
                                    throws NotFoundException {
                                try {
                                    return super.getString(id, formatArgs);
                                } catch (IllegalFormatConversionException ifce) {
                                    Log.e("DatePickerDialogFix",
                                            "IllegalFormatConversionException Fixed!", ifce);
                                    String template = super.getString(id);
                                    template = template.replaceAll("%" + ifce.getConversion(),
                                            "%s");
                                    return String.format(getConfiguration().locale, template,
                                            formatArgs);
                                }
                            }
                        };
                    }
                    return wrappedResources;
                }
            };
        }

        if (dateTime == null) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        } else {
            year = dateTime.getYear();
            month = dateTime.getMonthOfYear() - 1;
            day = dateTime.getDayOfMonth();
        }

        // Create a new instance of DatePickerDialog and return it
        dialog = new DatePickerDialog(context, this, year, month, day);

        return dialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (onDateSetListener != null) {
            onDateSetListener.onDateSet(view, year, month + 1, day);
        }

    }

    private static boolean isBrokenSamsungDevice() {
        return (Build.MANUFACTURER.equalsIgnoreCase("samsung")
                && isBetweenAndroidVersions(
                Build.VERSION_CODES.LOLLIPOP,
                Build.VERSION_CODES.LOLLIPOP_MR1));
    }

    private static boolean isBetweenAndroidVersions(int min, int max) {
        return Build.VERSION.SDK_INT >= min && Build.VERSION.SDK_INT <= max;
    }
}
