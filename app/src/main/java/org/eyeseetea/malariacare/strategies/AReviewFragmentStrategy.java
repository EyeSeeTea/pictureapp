package org.eyeseetea.malariacare.strategies;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.eyeseetea.malariacare.layout.adapters.dashboard.IDashboardAdapter;
import org.eyeseetea.malariacare.layout.adapters.dashboard.ReviewScreenAdapter;


public abstract class AReviewFragmentStrategy {

    public void initListView(LayoutInflater lInflater, IDashboardAdapter adapter,
            ListView listView) {
        //inflate headers
        View header = lInflater.inflate(adapter.getHeaderLayout(), null, false);
        View subHeader = lInflater.inflate(
                ((ReviewScreenAdapter) adapter).getSubHeaderLayout(), null, false);

        //set headers and list in the listview
        listView.addHeaderView(header);
        listView.addHeaderView(subHeader);
        listView.setAdapter((BaseAdapter) adapter);

        //remove spaces between rows in the listview
        listView.setDividerHeight(0);
    }
}
