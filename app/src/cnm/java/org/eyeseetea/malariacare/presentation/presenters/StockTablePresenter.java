package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.entity.DrugValues;
import org.eyeseetea.malariacare.domain.usecase.GetStockTableValuesUseCase;
import org.eyeseetea.malariacare.utils.Constants;

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
        mView.initRecyclerView();
        mView.initAddButtons();
        getStockValues();
    }

    private void getStockValues() {
        mGetStockTableValuesUseCase.execute(new GetStockTableValuesUseCase.Callback() {
            @Override
            public void onGetStockValues(List<DrugValues> drugValues) {
                if (mView != null) {
                    mView.showStockValues(drugValues);
                }
            }

            @Override
            public void onError(Exception e) {
                if (mView != null) {
                    mView.showError(e);
                }
            }
        });
    }


    public void detachView() {
        mView = null;
    }

    public void reloadData() {
        getStockValues();
    }

    public void onAddReceiptClick() {
        mView.showNewReceiptSurvey(Constants.SURVEY_RECEIPT);
    }

    public void onAddExpenseClick() {
        mView.showNewReceiptSurvey(Constants.SURVEY_ISSUE);
    }


    public interface View {
        void showNewReceiptSurvey(int type);

        void initRecyclerView();

        void showStockValues(List<DrugValues> drugsValuesList);

        void initAddButtons();

        void showError(Exception e);
    }


}
