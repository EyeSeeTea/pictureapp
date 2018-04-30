package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.datasources.QuestionLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IQuestionRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.StockSummary;
import org.eyeseetea.malariacare.domain.usecase.GetStockSummaryUseCase;
import org.eyeseetea.malariacare.layout.adapters.survey.StockTableAdapter;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.presentation.presenters.StockSummaryPresenter;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class StockSummaryFragment extends Fragment implements StockSummaryPresenter.View,
        IDashboardFragment {
    private StockSummaryPresenter mStockSummaryPresenter;
    private View mView;
    private boolean isAddShowing;

    private RecyclerView mStockTable;
    private StockTableAdapter mStockTableAdapter;
    private UpdateSurveyReceiver mUpdateSurveyReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.stock_summary_fragment,
                container, false);
        initPresenter();
        return mView;
    }

    @Override
    public void initRecyclerView() {
        mStockTable = (RecyclerView) mView.findViewById(R.id.stock_table);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mStockTable.setLayoutManager(layoutManager);
        mStockTableAdapter = new StockTableAdapter(new ArrayList<StockSummary>());
        mStockTable.setAdapter(mStockTableAdapter);
    }


    private void initPresenter() {
        IAsyncExecutor asyncExecutor = new AsyncExecutor();
        IMainExecutor mainExecutor = new UIThreadExecutor();
        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        IQuestionRepository questionRepository = new QuestionLocalDataSource();
        GetStockSummaryUseCase getStockSummaryUseCase = new GetStockSummaryUseCase(
                asyncExecutor, mainExecutor, surveyRepository, questionRepository);
        mStockSummaryPresenter = new StockSummaryPresenter(getStockSummaryUseCase);
        mStockSummaryPresenter.attachView(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mStockSummaryPresenter.detachView();
    }

    @Override
    public void showStockValues(List<StockSummary> drugsValuesList) {
        mStockTableAdapter.replaceValues(drugsValuesList);
    }


    @Override
    public void reloadData() {
        if (mStockSummaryPresenter != null) {
            mStockSummaryPresenter.reloadData();
        }
    }

    @Override
    public void showNewReceiptSurvey(int type) {
        showNewReceiptBalanceFragment(type);
    }


    @Override
    public void initAddButtons() {
        final ImageButton addReceipt = (ImageButton) mView.findViewById(R.id.add_receipt_survey);
        final ImageButton addExpense = (ImageButton) mView.findViewById(R.id.add_expense_survey);
        final LinearLayout receiptContainer =
                (LinearLayout) mView.findViewById(R.id.add_receipt_container);
        final LinearLayout expenseContainer = (LinearLayout) mView.findViewById(
                R.id.add_expense_container);
        mView.findViewById(R.id.add_stock_survey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAddShowing) {
                    hideAddMenu(receiptContainer, expenseContainer);
                } else {
                    showAddMenu(receiptContainer, expenseContainer);
                }
            }
        });
        addReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAddMenu(receiptContainer, expenseContainer);
                mStockSummaryPresenter.onAddReceiptClick();
            }
        });
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAddMenu(receiptContainer, expenseContainer);
                mStockSummaryPresenter.onAddExpenseClick();
            }
        });
    }

    @Override
    public void showError(Exception e) {
        Toast.makeText(getActivity(), R.string.stock_table_error, Toast.LENGTH_LONG).show();
    }

    private void showAddMenu(LinearLayout receiptContainer,
            LinearLayout expenseContainer) {
        isAddShowing = true;
        receiptContainer.animate().translationY(
                -getActivity().getResources().getDimension(
                        R.dimen.smaller_translation));
        expenseContainer.animate().translationY(
                -getActivity().getResources().getDimension(
                        R.dimen.bigger_translation));
        mView.findViewById(R.id.add_receipt_text).setVisibility(View.VISIBLE);
        mView.findViewById(R.id.add_expense_text).setVisibility(View.VISIBLE);
    }

    private void hideAddMenu(LinearLayout receiptContainer,
            LinearLayout expenseContainer) {
        isAddShowing = false;
        receiptContainer.animate().translationY(0);
        expenseContainer.animate().translationY(0);
        mView.findViewById(R.id.add_receipt_text).setVisibility(View.GONE);
        mView.findViewById(R.id.add_expense_text).setVisibility(View.GONE);
    }


    private void showNewReceiptBalanceFragment(int type) {
        Activity activity = getActivity();
        if (activity != null) {
            AddBalanceReceiptFragment addBalanceReceiptFragment =
                    AddBalanceReceiptFragment.newInstance(type);
            replaceFragment(activity, R.id.dashboard_stock_table_container,
                    addBalanceReceiptFragment);

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

    @Override
    public void reloadHeader(Activity activity) {
        DashboardHeaderStrategy.getInstance().init(activity, R.string.fragment_stock_table);
    }

    @Override
    public void registerFragmentReceiver() {
        if (mUpdateSurveyReceiver == null) {
            mUpdateSurveyReceiver = new UpdateSurveyReceiver();
        }
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mUpdateSurveyReceiver,
                new IntentFilter(SurveyService.RELOAD_DASHBOARD_ACTION));
    }

    @Override
    public void unregisterFragmentReceiver() {
        if (mUpdateSurveyReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                    mUpdateSurveyReceiver);
            mUpdateSurveyReceiver = null;
        }
    }

    public class UpdateSurveyReceiver extends BroadcastReceiver {
        private UpdateSurveyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mStockSummaryPresenter != null && intent.getAction() != null
                    && intent.getAction().equals(SurveyService.RELOAD_DASHBOARD_ACTION)) {
                mStockSummaryPresenter.reloadData();
            }
        }
    }
}
