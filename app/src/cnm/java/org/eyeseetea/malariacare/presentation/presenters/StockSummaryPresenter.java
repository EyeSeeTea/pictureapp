package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.entity.StockSummary;
import org.eyeseetea.malariacare.domain.usecase.GetStockSummaryUseCase;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

public class StockSummaryPresenter {
    View mView;
    private GetStockSummaryUseCase mGetStockSummaryUseCase;

    public StockSummaryPresenter(
            GetStockSummaryUseCase getStockSummaryUseCase) {
        mGetStockSummaryUseCase = getStockSummaryUseCase;
    }

    public void attachView(View view) {
        mView = view;
        mView.initRecyclerView();
        mView.initAddButtons();
        getStockValues();
    }

    private void getStockValues() {
        mGetStockSummaryUseCase.execute(new GetStockSummaryUseCase.Callback() {
            @Override
            public void onGetStockValues(List<StockSummary> drugValues) {
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

        void showStockValues(List<StockSummary> drugsValuesList);

        void initAddButtons();

        void showError(Exception e);
    }


}
