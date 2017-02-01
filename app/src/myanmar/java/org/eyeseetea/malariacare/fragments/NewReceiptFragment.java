package org.eyeseetea.malariacare.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.strategies.DashboardActivityStrategy;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;
import org.hisp.dhis.android.sdk.persistence.models.Constant;

import java.util.Calendar;
import java.util.Date;


public class NewReceiptFragment extends Fragment {
    public static final String TAG = ".NewReceiptFragment";

    private static EditText date, rdt, act6, act12, act18, act24, pq, cq;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_new_receipt,
                container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        date = (EditText) view.findViewById(R.id.new_receipt_balance_date);
        rdt = (EditText) view.findViewById(R.id.new_receipt_balance_rdt);
        act6 = (EditText) view.findViewById(R.id.new_receipt_balance_act6);
        act12 = (EditText) view.findViewById(R.id.new_receipt_balance_act12);
        act18 = (EditText) view.findViewById(R.id.new_receipt_balance_act18);
        act24 = (EditText) view.findViewById(R.id.new_receipt_balance_act24);
        pq = (EditText) view.findViewById(R.id.new_receipt_balance_pq);
        cq = (EditText) view.findViewById(R.id.new_receipt_balance_cq);


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

    }

    private void submitPressed() {
        createNewSurvey();
        DashboardActivityStrategy mDashboardActivityStrategy = new DashboardActivityStrategy();
        mDashboardActivityStrategy.showStockFragment(getActivity(), false);
        ((DashboardActivity) getActivity()).closeNewReceiptFragment();
    }

    private void backPressed() {
        DashboardActivityStrategy mDashboardActivityStrategy = new DashboardActivityStrategy();
        mDashboardActivityStrategy.showStockFragment(getActivity(), false);
        ((DashboardActivity) getActivity()).closeNewReceiptFragment();
    }


    private void createNewSurvey() {
        Survey survey = new Survey(null, Program.getStockProgram(), Session.getUser(),
                Constants.SURVEU_RECEIP);
        survey.setEventDate(Calendar.getInstance().getTime());
        survey.save();
        new Value(rdt.getText().toString(), Question.getRDTQuestion(), survey).save();
        new Value(act6.getText().toString(), Question.getACT6Question(), survey).save();
        new Value(act12.getText().toString(), Question.getACT12Questions(), survey).save();
        new Value(act18.getText().toString(), Question.getACT18Questions(), survey).save();
        new Value(act24.getText().toString(), Question.getACT24Questions(), survey).save();
        new Value(pq.getText().toString(), Question.getPqQuestion(), survey).save();
        new Value(cq.getText().toString(), Question.getCqQuestion(), survey).save();
    }
}
