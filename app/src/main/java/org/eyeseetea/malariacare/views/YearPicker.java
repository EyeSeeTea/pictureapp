package org.eyeseetea.malariacare.views;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import org.eyeseetea.malariacare.R;
import org.joda.time.DateTime;


public class YearPicker extends DialogFragment {
    private int YEAR_INTERVAL = 200;

    private OnYearSelectedListener mOnYearSelectedListener;
    private NumberPicker mNumberPicker;
    private Button ok, cancel;
    private int maxYear, minYear;
    private Context mContext;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        mContext = context;
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.fragment_year_picker);
        setMaxMinYears();
        initViews(dialog);
        return dialog;
    }

    private void initViews(final Dialog dialog) {
        mNumberPicker = (NumberPicker) dialog.findViewById(R.id.year_picker);
        mNumberPicker.setMaxValue(maxYear);
        mNumberPicker.setMinValue(minYear);
        mNumberPicker.setValue(maxYear);
        ok = (Button) dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnYearSelectedListener != null) {
                    mOnYearSelectedListener.onYearSelected(mNumberPicker.getValue());
                    dialog.cancel();
                }
            }
        });
        cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }

    private void setMaxMinYears() {
        DateTime dateTime = new DateTime();
        maxYear = dateTime.getYear();
        minYear = dateTime.getYear() - YEAR_INTERVAL;
    }


    public void setOnYearSelectedListener(
            OnYearSelectedListener onYearSelectedListener) {
        mOnYearSelectedListener = onYearSelectedListener;
    }

    public interface OnYearSelectedListener {
        void onYearSelected(int year);
    }

    public void setButtonFont(int font, int textSize) {
        Typeface type = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(font));
        int realTextSize= mContext.getResources().getDimensionPixelSize(textSize);

        ok.setTypeface(type);
        ok.setTextSize(realTextSize);
        cancel.setTypeface(type);
        cancel.setTextSize(realTextSize);
    }

}
