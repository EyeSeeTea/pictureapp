package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.usecase.GetSurveysByProgram;
import org.eyeseetea.malariacare.layout.adapters.dashboard.StockSurveysAdapter;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.presentation.presenters.StockSurveysPresenter;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.malariacare.utils.Constants;

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
        DashboardHeaderStrategy.getInstance().init(activity, R.string.tab_tag_stock);
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
    }

    @Override
    public void unregisterFragmentReceiver() {
    }

    @Override
    public void showValues(List<Survey> surveys) {
        mStockSurveysAdapter.addSurveys(surveys);
        mStockSurveysAdapter.notifyDataSetChanged();
    }


    private void initializePresenter() {
        IMainExecutor mainExecutor = new UIThreadExecutor();
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        mStockSurveysPresenter = new StockSurveysPresenter(
                new GetSurveysByProgram(asyncExecutor, mainExecutor, surveyRepository),
                getResources().getString(R.string.stock_program_uid));
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
        final ImageButton addExpense = (ImageButton) mView.findViewById(R.id.add_expense_survey);
        mView.findViewById(R.id.add_stock_survey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAddShowing) {
                    hideAddMenu(addReceipt, addBalance, addExpense);
                } else {
                    showAddMenu(addReceipt, addBalance, addExpense);
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
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStockSurveysPresenter.onAddExpenseClick();
            }
        });
    }

    @Override
    public void showNewReceiptSurvey(int type) {
        showNewReceiptBalanceFragment(type);
    }

    private void showAddMenu(ImageButton receiptButton, ImageButton balanceButton,
            ImageButton expenseButton) {
        isAddShowing = true;
        receiptButton.animate().translationY(
                -getActivity().getResources().getDimension(
                        R.dimen.smaller_translation));
        balanceButton.animate().translationY(
                -getActivity().getResources().getDimension(
                        R.dimen.bigger_translation));
        expenseButton.animate().translationY(
                -getActivity().getResources().getDimension(
                        R.dimen.biggest_translation));
    }

    private void hideAddMenu(ImageButton receiptButton, ImageButton balanceButton,
            ImageButton expenseButton) {
        isAddShowing = false;
        receiptButton.animate().translationY(0);
        balanceButton.animate().translationY(0);
        expenseButton.animate().translationY(0);
    }


    private void showNewReceiptBalanceFragment(int type) {
        Activity activity = getActivity();
        if (activity != null) {
            AddBalanceReceiptFragment addBalanceReceiptFragment =
                    AddBalanceReceiptFragment.newInstance(type);
            replaceFragment(activity, R.id.dashboard_stock_container, addBalanceReceiptFragment);

            int headerString = R.string.fragment_new_receipt;
            if (type == Constants.SURVEY_RESET) {
                headerString = R.string.fragment_new_balance;
            } else if (type == Constants.SURVEY_ISSUE) {
                headerString = R.string.fragment_new_expense;
            }
            DashboardHeaderStrategy.getInstance().init(activity, headerString);
            if (activity instanceof DashboardActivity) {
                ((DashboardActivity) activity).initNewReceiptFragment();
            }
        }
    }

    private void replaceFragment(Activity activity, int layout, Fragment fragment) {
        if (activity != null) {
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            ft.replace(layout, fragment);
            ft.commit();
        }
    }

}
