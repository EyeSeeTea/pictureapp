package org.eyeseetea.malariacare.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.TreatmentQueries;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.DatePickerFragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;


public class NewReceiptBalanceFragment extends Fragment {
    public static final String TAG = ".NewReceiptBalance";
    public static final String TYPE = "newReceiptBalanceType";

    private EditText rdt, act6, act12, act18, act24, pq, cq;
    private TextView date;

    private int type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_new_receipt_balance,
                container, false);
        manageBundle(savedInstanceState);
        initViews(view);
        return view;
    }

    private void manageBundle(Bundle savedInstanceState) {
        Bundle bundle = (savedInstanceState == null) ? getArguments() : savedInstanceState;
        type = bundle.getInt(TYPE);
    }


    private void initViews(View view) {
        date = (TextView) view.findViewById(R.id.new_receipt_balance_date);
        rdt = (EditText) view.findViewById(R.id.new_receipt_balance_rdt);
        act6 = (EditText) view.findViewById(R.id.new_receipt_balance_act6);
        act12 = (EditText) view.findViewById(R.id.new_receipt_balance_act12);
        act18 = (EditText) view.findViewById(R.id.new_receipt_balance_act18);
        act24 = (EditText) view.findViewById(R.id.new_receipt_balance_act24);
        pq = (EditText) view.findViewById(R.id.new_receipt_balance_pq);
        cq = (EditText) view.findViewById(R.id.new_receipt_balance_cq);

        putTodayDate();
        view.findViewById(R.id.new_receipt_balance_back).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backPressed();
                    }
                });
        view.findViewById(R.id.new_receipt_balance_submit).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitPressed();
                    }
                });

        final DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                NumberFormat formatter = new DecimalFormat("00");
                date.setText(year + "/" + formatter.format(monthOfYear) + "/" + formatter.format(
                        dayOfMonth));
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getFragmentManager(), TAG);
            }
        });
    }

    private void putTodayDate() {
        Date todayDate = Calendar.getInstance().getTime();
        date.setText(Utils.parseDateToString(todayDate, "yyyy/MM/dd"));
    }

    private void submitPressed() {
        createNewSurvey();
        closeFragment();
    }

    private void backPressed() {
        closeFragment();
    }

    private void closeFragment() {
        ((DashboardActivity) getActivity()).closeReceiptBalanceFragment();
    }


    private void createNewSurvey() {
        Survey survey = new Survey(null, Program.findByUID(
                PreferencesState.getInstance().getContext().getString(R.string.stockProgramUID)),
                Session.getUser(), type);
        Calendar surveyDate;
        if (date.getText().toString().isEmpty()) {
            surveyDate = Calendar.getInstance();
        } else {
            surveyDate = Utils.parseStringToCalendar(date.getText().toString(), "yyyy/MM/dd");
        }
        survey.setEventDate(surveyDate.getTime());
        survey.setStatus(Constants.SURVEY_COMPLETED);
        survey.save();
        new Value(rdt.getText().toString().isEmpty() ? rdt.getHint().toString()
                : rdt.getText().toString(), TreatmentQueries.getStockRDTQuestion(), survey).save();
        new Value(act6.getText().toString().isEmpty() ? act6.getHint().toString()
                : act6.getText().toString(), TreatmentQueries.getACT6Question(), survey).save();
        new Value(act12.getText().toString().isEmpty() ? act12.getHint().toString()
                : act12.getText().toString(), TreatmentQueries.getACT12Question(), survey).save();
        new Value(act18.getText().toString().isEmpty() ? act18.getHint().toString()
                : act18.getText().toString(), TreatmentQueries.getACT18Question(), survey).save();
        new Value(act24.getText().toString().isEmpty() ? act24.getHint().toString()
                : act24.getText().toString(), TreatmentQueries.getACT24Question(), survey).save();
        new Value(pq.getText().toString().isEmpty() ? pq.getHint().toString()
                : pq.getText().toString(), TreatmentQueries.getPqQuestion(), survey).save();
        new Value(cq.getText().toString().isEmpty() ? cq.getHint().toString()
                : cq.getText().toString(), TreatmentQueries.getCqQuestion(), survey).save();
    }
}