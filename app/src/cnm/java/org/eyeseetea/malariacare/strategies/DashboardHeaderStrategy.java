package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.fragments.DashboardSentFragment;

import java.util.List;

public class DashboardHeaderStrategy extends ADashboardHeaderStrategy {

    @Override
    public View loadHeader(Integer headerLayout, LayoutInflater inflater) {
        return inflater.inflate(headerLayout, null, false);
    }

    @Override
    public void init(Activity activity, int tabTag) {
        View headerView = activity.findViewById(R.id.common_header);

        TextView headerTitle = (TextView) headerView.findViewById(R.id.header_title);
        TextView headerExplanation = (TextView) headerView.findViewById(R.id.header_explanation);

        switch (tabTag) {
            case R.string.tab_tag_assess:
                headerTitle.setText(R.string.new_cases);
                headerExplanation.setText(R.string.cases_pending);
                break;
            case R.string.tab_tag_improve:
                headerTitle.setText(R.string.past_cases);
                headerExplanation.setText(R.string.cases_history);
                break;
            case R.string.tab_tag_monitor:
                headerTitle.setText(R.string.monitor_button);
                headerExplanation.setText("");
                break;
        }
    }

    @Override
    public void hideHeader(Activity activity) {
    }

    public View loadFilter(LayoutInflater inflater) {
        return null;
    }

    public void initFilters(DashboardSentFragment dashboardSentFragment, ListView listView,
            List<SurveyDB> surveysFromService) {
    }
}
