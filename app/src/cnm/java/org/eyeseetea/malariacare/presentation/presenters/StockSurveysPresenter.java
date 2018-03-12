package org.eyeseetea.malariacare.presentation.presenters;

import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.usecase.GetSurveysByProgram;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

public class StockSurveysPresenter {


    public interface StockView {
        void showValues(List<Survey> surveys);

        void initRecyclerView();

        void initAddButtons();

        void showNewReceiptSurvey(int type);
    }

    StockView mStockView;
    private GetSurveysByProgram mGetSurveysByProgram;
    private String mUidStock;

    public StockSurveysPresenter(
            GetSurveysByProgram getSurveysByProgram, String uidStock) {
        mGetSurveysByProgram = getSurveysByProgram;
        mUidStock = uidStock;
    }

    public void attachView(StockView stockView) {
        mStockView = stockView;
        mStockView.initRecyclerView();
        mStockView.initAddButtons();
        getStockSurveys();
    }

    private void getStockSurveys() {
        mGetSurveysByProgram.execute(new GetSurveysByProgram.Callback() {
            @Override
            public void onGetSurveys(List<Survey> surveys) {
                if (mStockView != null) {
                    mStockView.showValues(surveys);
                }
            }
        }, mUidStock);
    }

    public void detachView() {
        mStockView = null;
    }

    public void onAddBalanceClick() {
        mStockView.showNewReceiptSurvey(Constants.SURVEY_RESET);
    }

    public void onAddReceiptClick() {
        mStockView.showNewReceiptSurvey(Constants.SURVEY_RECEIPT);
    }

    public void onAddExpenseClick() {
        mStockView.showNewReceiptSurvey(Constants.SURVEY_ISSUE);
    }

    public void reloadData() {
        getStockSurveys();
    }
}
