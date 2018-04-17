package org.eyeseetea.malariacare.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eyeseetea.malariacare.presentation.presenters.StockTablePresenter;

public class StockTableFragment extends Fragment implements StockTablePresenter.View {
    StockTablePresenter mStockTablePresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        initPresenter();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void initPresenter() {
        mStockTablePresenter = new StockTablePresenter();
        mStockTablePresenter.attachView(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mStockTablePresenter.dettachView();
    }

    @Override
    public void showStockValues() {

    }

    @Override
    public void showError() {

    }
}
