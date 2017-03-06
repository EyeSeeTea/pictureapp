package org.eyeseetea.malariacare.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.layout.adapters.HistoricReceiptBalanceAdapter;

import java.util.List;

public class HistoricReceiptBalanceFragment extends Fragment {
    public static final String TAG = ".historicReceiptBalance";
    public static final String TYPE = "historicReceiptBalanceType";

    private RecyclerView historicList;
    private RecyclerView.LayoutManager mLayoutManager;
    private HistoricReceiptBalanceAdapter mHistoricReceiptBalanceAdapter;

    private List<Survey> mSurveys;
    private int type;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_historic_receipt_balance,
                container, false);
        manageBundle(savedInstanceState);
        initData();
        initViews(view);
        return view;
    }


    private void manageBundle(Bundle savedInstanceState) {
        Bundle bundle = (savedInstanceState == null) ? getArguments() : savedInstanceState;
        type = bundle.getInt(TYPE);
    }

    private void initData() {
        mSurveys = Survey.getSurveysWithProgramType(Program.getStockProgram(), type);
    }


    private void initViews(View view) {
        historicList = (RecyclerView) view.findViewById(R.id.historic_receipt_balance_list);

        mLayoutManager = new LinearLayoutManager(getActivity());
        historicList.setLayoutManager(mLayoutManager);

        mHistoricReceiptBalanceAdapter = new HistoricReceiptBalanceAdapter(mSurveys,type);
        historicList.setAdapter(mHistoricReceiptBalanceAdapter);

        view.findViewById(R.id.historic_receipt_balance_back).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backPressed();
                    }
                });

    }

    private void backPressed() {
        ((DashboardActivity) getActivity()).closeReceiptBalanceFragment();
    }
}
