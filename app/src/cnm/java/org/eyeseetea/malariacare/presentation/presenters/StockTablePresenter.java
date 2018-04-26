package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.entity.DrugValues;
import org.eyeseetea.malariacare.domain.usecase.GetStockTableValuesUseCase;

import java.util.List;

public class StockTablePresenter {
    View mView;
    private GetStockTableValuesUseCase mGetStockTableValuesUseCase;

    public StockTablePresenter(
            GetStockTableValuesUseCase getStockTableValuesUseCase) {
        mGetStockTableValuesUseCase = getStockTableValuesUseCase;
    }

    public void attachView(View view) {
        mView = view;
        getStockValues();
    }

    private void getStockValues() {
        mGetStockTableValuesUseCase.execute(new GetStockTableValuesUseCase.Callback() {
            @Override
            public void onGetStockValues(List<DrugValues> drugValues) {
                mView.showStockValues(drugValues);
            }
        });
    }


    public void detachView() {
        mView = null;
    }

    public void reloadData() {
        getStockValues();
    }


    public interface View {
        void showStockValues(List<DrugValues> drugsValuesList);

        void showError();

    }


}
