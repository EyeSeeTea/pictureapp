package org.eyeseetea.malariacare.strategies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.fragments.DashboardSentFragment;

import java.util.List;

public class DashboardHeaderStrategy  extends  ADashboardHeaderStrategy{

    @Override
    public View loadHeader(Integer headerLayout, LayoutInflater inflater) {
        return inflater.inflate(headerLayout, null, false);
    }

    @Override
    public void init(Activity activity, int tabTag) {

    }

    @Override
    public void hideHeader(Activity activity) {
    }

    public View loadFilter(LayoutInflater inflater) {
        return null;
    }

    public void initFilters(DashboardSentFragment dashboardSentFragment, ListView listView,
            List<Survey> surveysFromService) {
        return;
    }
}
