package org.eyeseetea.malariacare.strategies;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.fragments.AddBalanceReceiptFragment;
import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;
import org.eyeseetea.malariacare.layout.adapters.dashboard.AssessmentAdapter;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

public class DashboardUnsentFragmentStrategy extends ADashboardUnsentFragmentStrategy {
    public static final String IS_STOCK_FRAGMENT = "isStockFragment";

    private boolean isStockFragment;
    private boolean isAddShowing = false;

    public DashboardUnsentFragmentStrategy(
            DashboardUnsentFragment dashboardUnsentFragment) {
        super(dashboardUnsentFragment);
    }

    @Override
    public void registerSurveyReceiver(Activity activity,
            DashboardUnsentFragment.SurveyReceiver surveyReceiver) {
        if (isStockFragment) {
            LocalBroadcastManager.getInstance(activity).registerReceiver(surveyReceiver,
                    new IntentFilter(SurveyService.GET_SURVEYS_FROM_PROGRAM));
        } else {
            super.registerSurveyReceiver(activity, surveyReceiver);
        }
    }

    @Override
    public void saveBundle(Bundle outState) {
        super.saveBundle(outState);
        outState.putBoolean(IS_STOCK_FRAGMENT, isStockFragment);
    }

    @Override
    public View inflateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Bundle bundle = savedInstanceState;
        if (bundle == null) {
            bundle = mDashboardUnsentFragment.getArguments();
        }
        if (bundle != null) {
            isStockFragment = bundle.getBoolean(IS_STOCK_FRAGMENT, false);
        }
        if (isStockFragment) {
            View view = inflater.inflate(R.layout.stock_list_fragemnt, container, false);
            initViews(view);
            reloadData();
            reloadHeader(mDashboardUnsentFragment.getActivity());
            return view;
        } else {
            return super.inflateView(inflater, container, savedInstanceState);
        }
    }

    private void initViews(View view) {
        final ImageButton addBalance = (ImageButton) view.findViewById(R.id.add_balance_survey);
        final ImageButton addReceipt = (ImageButton) view.findViewById(R.id.add_receipt_survey);
        final ImageButton addExpense = (ImageButton) view.findViewById(R.id.add_expense_survey);
        view.findViewById(R.id.add_stock_survey).setOnClickListener(new View.OnClickListener() {
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
            public void onClick(View view) {
                showNewReceiptBalanceFragment(Constants.SURVEY_RESET);
            }
        });
        addReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewReceiptBalanceFragment(Constants.SURVEY_RECEIPT);
            }
        });
        addExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewReceiptBalanceFragment(Constants.SURVEY_ISSUE);
            }
        });
    }

    private void showNewReceiptBalanceFragment(int type) {
        Activity activity = mDashboardUnsentFragment.getActivity();
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


    @Override
    public void manageOnItemClick(AssessmentAdapter adapter, ListView l, int position,
            List<SurveyDB> surveyDBs) {
        if (!isStockFragment) {
            super.manageOnItemClick(adapter, l, position, surveyDBs);
        }
    }

    @Override
    public void reloadHeader(Activity activity) {
        if (isStockFragment) {
            mDashboardUnsentFragment.reloadHeader(activity, R.string.tab_tag_stock);
        } else {
            super.reloadHeader(activity);
        }
    }

    @Override
    public void reloadData() {
        if (isStockFragment) {
            //Reload data using service
            Intent surveysIntent = new Intent(
                    PreferencesState.getInstance().getContext().getApplicationContext(),
                    SurveyService.class);
            surveysIntent.putExtra(SurveyService.SERVICE_METHOD,
                    SurveyService.GET_SURVEYS_FROM_PROGRAM);
            surveysIntent.putExtra(SurveyService.PROGRAM_UID,
                    mDashboardUnsentFragment.getActivity().getString(R.string.stock_program_uid));
            PreferencesState.getInstance().getContext().getApplicationContext().startService(
                    surveysIntent);
        } else {
            super.reloadData();
        }
    }

    @Override
    public void onReceiveSurveys(Intent intent) {
        super.onReceiveSurveys(intent);
        if (isStockFragment && SurveyService.GET_SURVEYS_FROM_PROGRAM.equals(intent.getAction())) {
            List<SurveyDB> surveysUnsentFromService;
            Session.valuesLock.readLock().lock();
            try {
                surveysUnsentFromService = (List<SurveyDB>) Session.popServiceValue(
                        SurveyService.GET_SURVEYS_FROM_PROGRAM);
            } finally {
                Session.valuesLock.readLock().unlock();
            }
            mDashboardUnsentFragment.reloadSurveysFromService(surveysUnsentFromService);
        }
    }


    private void showAddMenu(ImageButton receiptButton, ImageButton balanceButton,
            ImageButton expenseButton) {
        isAddShowing = true;
        receiptButton.animate().translationY(
                -mDashboardUnsentFragment.getActivity().getResources().getDimension(
                        R.dimen.smaller_translation));
        balanceButton.animate().translationY(
                -mDashboardUnsentFragment.getActivity().getResources().getDimension(
                        R.dimen.bigger_translation));
        expenseButton.animate().translationY(
                -mDashboardUnsentFragment.getActivity().getResources().getDimension(
                        R.dimen.biggest_translation));
    }

    private void hideAddMenu(ImageButton receiptButton, ImageButton balanceButton,
            ImageButton expenseButton) {
        isAddShowing = false;
        receiptButton.animate().translationY(0);
        balanceButton.animate().translationY(0);
        expenseButton.animate().translationY(0);
    }
}
