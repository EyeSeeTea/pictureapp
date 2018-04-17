package org.eyeseetea.malariacare.presentation.presenters;

public class StockTablePresenter {
    View mView;

    public void attachView(View view) {
        mView = view;
    }

    public void dettachView() {
        mView = null;
    }

    public interface View {
        void showStockValues();

        void showError();

    }

}
