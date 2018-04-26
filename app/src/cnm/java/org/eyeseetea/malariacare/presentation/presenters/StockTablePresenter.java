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
        initValues();
    }

    private void initValues() {
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


    public interface View {
        void showStockValues(List<DrugValues> drugsValuesList);

        void showError();

    }


}
