package org.eyeseetea.malariacare.strategies;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.eyeseetea.malariacare.layout.adapters.dashboard.IDashboardAdapter;

public class ReviewFragmentStrategy extends AReviewFragmentStrategy {

    @Override
    public void initListView(LayoutInflater lInflater, IDashboardAdapter adapter,
            ListView listView) {
        //inflate header
        View header = lInflater.inflate(adapter.getHeaderLayout(), null, false);

        //set header and adapter in the listview
        listView.addHeaderView(header);
        listView.setAdapter((BaseAdapter) adapter);

        //remove spaces between rows in the listview
        listView.setDividerHeight(0);
    }
}
