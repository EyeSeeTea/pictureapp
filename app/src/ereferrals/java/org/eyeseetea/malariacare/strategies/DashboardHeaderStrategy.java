package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.fragments.DashboardSentFragment;
import org.eyeseetea.sdk.presentation.views.CustomTextView;
import java.util.List;

public class DashboardHeaderStrategy extends ADashboardHeaderStrategy {


    private static void setTitle(View view, int keyId, int titleString) {
        CustomTextView title = (CustomTextView) view.findViewById(keyId);
        title.setText(view.getContext().getString(titleString));
    }

    @Override
    public View loadHeader(Integer headerLayout, LayoutInflater inflater) {
        return null;
    }

    @Override
    public void init(Activity activity, int tabTag) {
        View headerView = getHeaderView(activity);
        int titleResource = 0, subtitleResource = 0;
        switch (tabTag) {
            case R.string.tab_tag_assess:
                titleResource = R.string.new_case_title;
                subtitleResource = R.string.unsent_data;
                break;
            case R.string.tab_tag_improve:
                titleResource = R.string.past_cases;
                subtitleResource = R.string.sent_data;
                break;
            case R.string.tab_tag_stock:
                titleResource = R.string.stock_control;
                subtitleResource = R.string.receipts_balances;
                break;
            case R.string.tab_tag_monitor:
                titleResource = R.string.monitoring_title;
                subtitleResource = R.string.monitoring_time_period;
                break;
            case R.string.fragment_new_receipt:
                titleResource = R.string.stock_control;
                subtitleResource=R.string.new_receipt;
                break;
            case R.string.fragment_new_reset:
                titleResource = R.string.stock_control;
                subtitleResource=R.string.new_reset;
                break;
            case R.string.fragment_historic_receipt_balance:
                titleResource = R.string.stock_control;
                subtitleResource=R.string.previous_distributions;
                break;
            case R.string.fragment_historic_reset:
                titleResource = R.string.stock_control;
                subtitleResource=R.string.previous_resets;
                break;
        }
        setTitle(headerView, R.id.header_title, titleResource);
        setTitle(headerView, R.id.header_subtitle, subtitleResource);
        headerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideHeader(Activity activity) {
        View headerView = getHeaderView(activity);
        if (headerView == null) return;
        headerView.setVisibility(View.GONE);
    }

    private View getHeaderView(Activity activity) {
        if (activity == null) {
            return null;
        }
        View headerView = activity.findViewById(R.id.common_header);
        if (headerView == null) {
            return null;
        }
        return headerView;
    }

    public View loadFilter(LayoutInflater inflater) {
        return null;
    }

    public void initFilters(DashboardSentFragment dashboardSentFragment, ListView listView,
            List<Survey> surveysFromService) {
        return;
    }
}
