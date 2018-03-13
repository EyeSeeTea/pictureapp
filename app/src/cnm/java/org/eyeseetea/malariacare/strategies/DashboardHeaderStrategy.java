package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
        ImageView headerImage = (ImageView) headerView.findViewById(R.id.header_image);

        switch (tabTag) {
            case R.string.tab_tag_assess:
                headerTitle.setText(R.string.new_cases);
                headerExplanation.setText(R.string.cases_pending);
                headerExplanation.setVisibility(View.VISIBLE);
                headerImage.setVisibility(View.GONE);
                break;
            case R.string.tab_tag_improve:
                headerTitle.setText(R.string.past_cases);
                headerExplanation.setText(R.string.cases_history);
                headerExplanation.setVisibility(View.VISIBLE);
                headerImage.setVisibility(View.GONE);
                break;
            case R.string.tab_tag_stock:
                headerTitle.setText(R.string.tab_stock);
                headerExplanation.setText("");
                headerImage.setVisibility(View.INVISIBLE);
                break;
            case R.string.fragment_new_balance:
                headerTitle.setText(R.string.tab_stock);
                headerExplanation.setVisibility(View.GONE);
                headerImage.setVisibility(View.VISIBLE);
                headerImage.setImageResource(R.drawable.ic_sheet_survey_balance);
                break;
            case R.string.fragment_new_receipt:
                headerTitle.setText(R.string.tab_stock);
                headerExplanation.setVisibility(View.GONE);
                headerImage.setVisibility(View.VISIBLE);
                headerImage.setImageResource(R.drawable.ic_arrow_survey_receipt);
                break;
            case R.string.fragment_new_expense:
                headerTitle.setText(R.string.tab_stock);
                headerExplanation.setVisibility(View.GONE);
                headerImage.setVisibility(View.VISIBLE);
                headerImage.setImageResource(R.drawable.ic_arrow_survey_expense);
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
