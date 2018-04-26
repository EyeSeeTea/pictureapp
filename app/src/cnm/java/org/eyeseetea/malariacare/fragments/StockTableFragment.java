package org.eyeseetea.malariacare.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.DrugValues;
import org.eyeseetea.malariacare.domain.usecase.GetStockTableValuesUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.presentation.presenters.StockTablePresenter;

import java.util.List;

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

    }

    @Override
    public void showError() {

    }
}
