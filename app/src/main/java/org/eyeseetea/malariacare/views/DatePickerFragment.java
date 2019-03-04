package org.eyeseetea.malariacare.views;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.IllegalFormatConversionException;


public class DatePickerFragment extends DialogFragment {

    private int year;
    private int month;
    private int day;
    private DateTime dateTime;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private AlertDialog dialog;

    private String title;

    private TextView titleTextView;
    private DatePicker datePicker;

    public void setOnDateSetListener(DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.onDateSetListener = onDateSetListener;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setTitle(String title) {
        this.title = title;

        if (titleTextView != null) {
            titleTextView.setText(title);
        }
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
            // Use the current date as the default date in the datePicker
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        } else {
            year = dateTime.getYear();
            month = dateTime.getMonthOfYear() - 1;
            day = dateTime.getDayOfMonth();
        }

        View view = getActivity().getLayoutInflater().inflate(R.layout.view_date_picker_with_title,
                null);

        titleTextView = view.findViewById(R.id.date_title_text_view);
        datePicker = view.findViewById(R.id.date_picker);

        titleTextView.setText(title);
        datePicker.setCalendarViewShown(false);
        datePicker.updateDate(year, month, day);


        dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setNegativeButton(context.getText(android.R.string.cancel), null)
                .setPositiveButton(context.getText(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (onDateSetListener != null) {
                                    onDateSetListener.onDateSet(datePicker, datePicker.getYear(),
                                            datePicker.getMonth() + 1, datePicker.getDayOfMonth());
                                }
                            }
                        })
                .create();

        return dialog;
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
