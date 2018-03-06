package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.layout.adapters.dashboard.StockSurveysAdapter;
import org.eyeseetea.malariacare.presentation.presenters.StockSurveysPresenter;

import java.util.List;

public class StockSurveysFragment extends Fragment implements IDashboardFragment,
        StockSurveysPresenter.StockView {

    private StockSurveysAdapter mStockSurveysAdapter;
    private StockSurveysPresenter mStockSurveysPresenter;
    private View mView;
    private boolean isAddShowing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_stock_surveys,
                container, false);
        initializePresenter();
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mStockSurveysPresenter.detachView();
    }

    @Override
    public void reloadData() {
        if (mStockSurveysPresenter != null) {
            mStockSurveysPresenter.reloadData();
        }
    }

    @Override
    public void reloadHeader(Activity activity) {
        if (mStockSurveysPresenter != null) {
            mStockSurveysPresenter.reloadHeader(activity);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerFragmentReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterFragmentReceiver();
    }

    @Override
    public void registerFragmentReceiver() {
        mStockSurveysPresenter.registerFragmentReceiver(getActivity());
    }

    @Override
    public void unregisterFragmentReceiver() {
        mStockSurveysPresenter.unregisterFragmentReceiver(getActivity());
    }

    @Override
    public void showValues(List<SurveyDB> surveys) {
        mStockSurveysAdapter.addSurveys(surveys);
        mStockSurveysAdapter.notifyDataSetChanged();
    }


    private void initializePresenter() {
        mStockSurveysPresenter = new StockSurveysPresenter();
        mStockSurveysPresenter.attachView(this);
    }

    @Override
    public void initRecyclerView() {
        RecyclerView stockSurveysRecycler = (RecyclerView) mView.findViewById(
                R.id.stock_surveys_list);
        mStockSurveysAdapter = new StockSurveysAdapter(getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        stockSurveysRecycler.setLayoutManager(mLayoutManager);
        stockSurveysRecycler.setAdapter(mStockSurveysAdapter);
    }

    @Override
    public void initAddButtons() {
        final ImageButton addBalance = (ImageButton) mView.findViewById(R.id.add_balance_survey);
        final ImageButton addReceipt = (ImageButton) mView.findViewById(R.id.add_receipt_survey);
        mView.findViewById(R.id.add_stock_survey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAddShowing) {
                    hideAddMenu(addReceipt, addBalance);
                } else {
                    showAddMenu(addReceipt, addBalance);
                }
            }
        });
        addBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStockSurveysPresenter.onAddBalanceClick();
            }
        });
        addReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStockSurveysPresenter.onAddReceiptClick();
            }
        });
    }

    private void showAddMenu(ImageButton receiptButton, ImageButton balanceButton) {
        isAddShowing = true;
        receiptButton.animate().translationY(
                -getActivity().getResources().getDimension(
                        R.dimen.smaller_translation));
        balanceButton.animate().translationY(
                -getActivity().getResources().getDimension(
                        R.dimen.bigger_translation));
    }

    private void hideAddMenu(ImageButton receiptButton, ImageButton balanceButton) {
        isAddShowing = false;
        receiptButton.animate().translationY(0);
        balanceButton.animate().translationY(0);
    }


}
