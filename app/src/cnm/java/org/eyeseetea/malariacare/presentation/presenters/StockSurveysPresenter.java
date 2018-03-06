package org.eyeseetea.malariacare.presentation.presenters;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.fragments.AddBalanceReceiptFragment;
import org.eyeseetea.malariacare.services.SurveyService;
import org.eyeseetea.malariacare.strategies.DashboardHeaderStrategy;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

public class StockSurveysPresenter {

    public interface StockView {
        void showValues(List<SurveyDB> surveys);

        void initRecyclerView();

        void initAddButtons();
    }

    StockView mStockView;
    private SurveyReceiver mSurveyReceiver;

    public void attachView(StockView stockView) {
        mStockView = stockView;
        mStockView.initRecyclerView();
        mStockView.initAddButtons();
    }

    public void detachView() {
        mStockView = null;
    }

    public void onAddBalanceClick(Activity activity) {
        showNewReceiptBalanceFragment(Constants.SURVEY_RESET, activity);
    }

    public void onAddReceiptClick(Activity activity) {
        showNewReceiptBalanceFragment(Constants.SURVEY_RECEIPT, activity);
    }

    public void reloadData() {
        Intent surveysIntent = new Intent(
                PreferencesState.getInstance().getContext().getApplicationContext(),
                SurveyService.class);
        surveysIntent.putExtra(SurveyService.SERVICE_METHOD,
                SurveyService.GET_SURVEYS_FROM_PROGRAM);
        surveysIntent.putExtra(SurveyService.PROGRAM_UID,
                PreferencesState.getInstance().getContext().getString(R.string.stock_program_uid));
        PreferencesState.getInstance().getContext().getApplicationContext().startService(
                surveysIntent);
    }

    public void reloadHeader(Activity activity) {

        DashboardHeaderStrategy.getInstance().init(activity, R.string.tab_tag_stock);
    }

    public void registerFragmentReceiver(Activity activity) {
        if (mSurveyReceiver == null) {
            mSurveyReceiver = new SurveyReceiver();
        }
        LocalBroadcastManager.getInstance(activity).registerReceiver(mSurveyReceiver,
                new IntentFilter(SurveyService.GET_SURVEYS_FROM_PROGRAM));
    }

    public void unregisterFragmentReceiver(Activity activity) {
        if (mSurveyReceiver != null) {
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(mSurveyReceiver);
            mSurveyReceiver = null;
        }
    }


    private void showNewReceiptBalanceFragment(int type, Activity activity) {
        if (activity != null) {
            AddBalanceReceiptFragment addBalanceReceiptFragment =
                    AddBalanceReceiptFragment.newInstance(type);
            replaceFragment(activity, R.id.dashboard_stock_container, addBalanceReceiptFragment);

            int headerString = R.string.fragment_new_receipt;
            if (type == Constants.SURVEY_RESET) {
                headerString = R.string.fragment_new_reset;
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


    /**
     * Inner private class that receives the result from the service
     */
    public class SurveyReceiver extends BroadcastReceiver {
        public SurveyReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(SurveyReceiver.class.getName(), "onReceive");
            if (SurveyService.GET_SURVEYS_FROM_PROGRAM.equals(
                    intent.getAction())) {

                List<SurveyDB> surveysUnsentFromService;
                Session.valuesLock.readLock().lock();
                try {
                    surveysUnsentFromService = (List<SurveyDB>) Session.popServiceValue(
                            SurveyService.GET_SURVEYS_FROM_PROGRAM);
                } finally {
                    Session.valuesLock.readLock().unlock();
                }
                mStockView.showValues(surveysUnsentFromService);
            }
        }
    }

}
