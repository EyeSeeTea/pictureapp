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

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.DrugValues;
import org.eyeseetea.malariacare.domain.usecase.GetStockTableValuesUseCase;
import org.eyeseetea.malariacare.layout.adapters.survey.StockTableAdapter;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.presentation.presenters.StockTablePresenter;

import java.util.ArrayList;
import java.util.List;

public class StockTableFragment extends Fragment implements StockTablePresenter.View ,IDashboardFragment{
    private StockTablePresenter mStockTablePresenter;
    private View mView;

    private RecyclerView mStockTable;
    private StockTableAdapter mStockTableAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        initPresenter();
        mView = inflater.inflate(R.layout.stock_table_fragment,
                container, false);
        initViews();
        return mView;
    }

    private void initViews() {
        mStockTable = (RecyclerView) mView.findViewById(R.id.stock_table);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mStockTable.setLayoutManager(layoutManager);
        mStockTableAdapter = new StockTableAdapter(new ArrayList<DrugValues>());
        mStockTable.setAdapter(mStockTableAdapter);
    }

    private void initPresenter() {
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        GetStockTableValuesUseCase getStockTableValuesUseCase = new GetStockTableValuesUseCase(
                asyncExecutor, mainExecutor, surveyRepository);
        mStockTablePresenter = new StockTablePresenter(getStockTableValuesUseCase);
        mStockTablePresenter.attachView(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mStockTablePresenter.detachView();
    }

    @Override
    public void showStockValues(List<DrugValues> drugsValuesList) {
        mStockTableAdapter.replaceValues(drugsValuesList);
    }

    @Override
    public void showError() {

    }

    @Override
    public void reloadData() {
        if(mStockTablePresenter!=null){
            mStockTablePresenter.reloadData();
        }
    }

    @Override
    public void reloadHeader(Activity activity) {

    }

    @Override
    public void registerFragmentReceiver() {

    }

    @Override
    public void unregisterFragmentReceiver() {

    }
}
