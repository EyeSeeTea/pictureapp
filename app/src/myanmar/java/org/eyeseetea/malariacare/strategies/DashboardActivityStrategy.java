package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.fragments.HistoricReceiptBalanceFragment;
import org.eyeseetea.malariacare.fragments.NewReceiptBalanceFragment;
import org.eyeseetea.malariacare.fragments.StockFragment;


public class DashboardActivityStrategy extends ADashboardActivityStrategy {
    private StockFragment stockFragment;

    @Override
    public void reloadStockFragment(Activity activity) {
        stockFragment.reloadData();
        stockFragment.reloadHeader(activity);
    }

    @Override
    public boolean showStockFragment(Activity activity, boolean isMoveToLeft) {
        stockFragment = new StockFragment();
        stockFragment.setArguments(activity.getIntent().getExtras());
        stockFragment.reloadData();
        stockFragment.reloadHeader(activity);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        if (isMoveToLeft) {
            isMoveToLeft = false;
            ft.setCustomAnimations(R.animator.anim_slide_in_right, R.animator.anim_slide_out_right);
        } else {
            ft.setCustomAnimations(R.animator.anim_slide_in_left, R.animator.anim_slide_out_left);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.dashboard_stock_container, stockFragment);
        ft.commit();
        return isMoveToLeft;
    }

    @Override
    public void newSurvey(Activity activity) {
        Program myanmarProgram = Program.findByUID(activity.getString(R.string.malariaProgramUID));
        Program stockProgram = Program.findByUID(activity.getString(R.string.stockProgramUID));
        // Put new survey in session
        Survey survey = new Survey(null, myanmarProgram, Session.getUser());
        survey.save();
        Session.setMalariaSurvey(survey);
        Survey stockSurvey = new Survey(null, stockProgram, Session.getUser());
        stockSurvey.save();
        Session.setStockSurvey(stockSurvey);
        prepareLocationListener(activity, survey);
    }

    @Override
    public boolean isHistoricNewReceiptBalanceFragment(Activity activity) {
        if (isFragmentActive(activity, HistoricReceiptBalanceFragment.class,
                R.id.dashboard_stock_container) || isFragmentActive(activity,
                NewReceiptBalanceFragment.class,
                R.id.dashboard_stock_container)) {
            return true;
        }
        return false;

    }

    private boolean isFragmentActive(Activity activity, Class fragmentClass, int layout) {
        Fragment currentFragment = activity.getFragmentManager().findFragmentById(layout);
        if (currentFragment.getClass().equals(fragmentClass)) {
            return true;
        }
        return false;
    }

}
