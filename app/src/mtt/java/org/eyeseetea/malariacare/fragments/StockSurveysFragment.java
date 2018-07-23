package org.eyeseetea.malariacare.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.usecase.GetSurveysByProgram;
import org.eyeseetea.malariacare.layout.adapters.dashboard.StockSurveysAdapter;
import org.eyeseetea.malariacare.layout.listeners.SwipeDismissRecyclerViewTouchListener;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;
import org.eyeseetea.malariacare.presentation.presenters.StockSurveysPresenter;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

public class StockSurveysFragment extends Fragment implements IDashboardFragment,
        StockSurveysPresenter.StockView {

    private StockSurveysAdapter mStockSurveysAdapter;
    private StockSurveysPresenter mStockSurveysPresenter;
    private View mView;
    private boolean isAddShowing;
    private UpdateSurveyReceiver mUpdateSurveyReceiver;

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

        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissRecyclerViewTouchListener touchListener =
                new SwipeDismissRecyclerViewTouchListener(
                        stockSurveysRecycler,
                        new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                if(position > -1 && position <= mStockSurveysAdapter.getItemCount()){
                                    long surveyId = mStockSurveysAdapter.getItemId(
                                            position );
                                    SurveyDB surveyDB = SurveyDB.findById(surveyId);
                                    if(surveyDB!=null && !surveyDB.isSent()
                                            && !surveyDB.isConflict()) {
                                        return true;
                                    }
                                }
                                return false;
                            }

                            @Override
                            public void onDismiss(final RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (final int position : reverseSortedPositions) {
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle(getActivity().getString(
                                                    R.string.dialog_title_delete_survey))
                                            .setMessage(getActivity().getString(
                                                    R.string.dialog_info_delete_survey))
                                            .setPositiveButton(android.R.string.yes,
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface arg0,
                                                                int arg1) {
                                                            long surveyId = mStockSurveysAdapter.getItemId(
                                                                    position );
                                                            SurveyDB surveyDB = SurveyDB.findById(surveyId);
                                                            if(surveyDB!=null && !surveyDB.isSent()
                                                                    && !surveyDB.isConflict()) {
                                                                surveyDB.delete();
                                                                reloadData();
                                                            }
                                                        }
                                                    })
                                            .setNegativeButton(android.R.string.no,
                                                    null).create().show();
                                }
                            }
                        });
        stockSurveysRecycler.setOnTouchListener(touchListener);
    }

    @Override
    public void initAddButtons() {
        final ImageButton addReceipt = (ImageButton) mView.findViewById(R.id.add_receipt_survey);
        final ImageButton addExpense = (ImageButton) mView.findViewById(R.id.add_expense_survey);
        final LinearLayout receiptContainer =
                (LinearLayout) mView.findViewById(R.id.add_receipt_container);
        final LinearLayout expenseContainer = (LinearLayout) mView.findViewById(
                R.id.add_expense_container);
        ;
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
                mStockSurveysPresenter.onAddReceiptClick();
            }
        });
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAddMenu(receiptContainer, expenseContainer);
                mStockSurveysPresenter.onAddExpenseClick();
            }
        });
    }

    @Override
    public void showNewReceiptSurvey(int type) {
        showNewReceiptBalanceFragment(type);
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

    public class UpdateSurveyReceiver extends BroadcastReceiver {
        private UpdateSurveyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mStockSurveysPresenter != null && intent.getAction() != null
                    && intent.getAction().equals(SurveyService.RELOAD_DASHBOARD_ACTION)) {
                mStockSurveysPresenter.reloadData();
            }
        }
    }

}
