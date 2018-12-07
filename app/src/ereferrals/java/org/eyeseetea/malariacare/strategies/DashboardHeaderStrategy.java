package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.fragments.DashboardSentFragment;
import org.eyeseetea.malariacare.network.ConnectivityStatus;
import org.eyeseetea.sdk.presentation.views.CustomTextView;

import java.util.List;

public class DashboardHeaderStrategy extends ADashboardHeaderStrategy {

    private CustomTextView headerText;

    private static void setTitle(View view, int keyId, int titleString) {
        CustomTextView title = (CustomTextView) view.findViewById(keyId);
        title.setText(view.getContext().getString(titleString));
    }

    @Override
    public View loadHeader(Integer headerLayout, LayoutInflater inflater) {
        View header = inflater.inflate(headerLayout, null, false);
        header.setVisibility(View.VISIBLE);
        return header;
    }

    @Override
    public void init(Activity activity, int tabTag) {
        View headerView = getHeaderView(activity);
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
        boolean notConnected = !ConnectivityStatus.isConnected(
                PreferencesState.getInstance().getContext());
        headerText = (CustomTextView) headerView.findViewById(R.id.header_text);
        headerText.translateText(
                notConnected ? R.string.unsent_dashboard_header_offline : R.string.online_status);
        return headerView;
    }

    public View loadFilter(LayoutInflater inflater) {
        return null;
    }

    public void initFilters(DashboardSentFragment dashboardSentFragment, ListView listView,
            List<SurveyDB> surveysFromService) {
        return;
    }
}
