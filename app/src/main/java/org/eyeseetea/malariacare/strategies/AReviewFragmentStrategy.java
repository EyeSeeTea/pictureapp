package org.eyeseetea.malariacare.strategies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ListView;

import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.fragments.ReviewFragment;
import org.eyeseetea.malariacare.layout.adapters.dashboard.IDashboardAdapter;
import org.eyeseetea.malariacare.layout.adapters.dashboard.ReviewScreenAdapter;

import java.util.ArrayList;
import java.util.List;


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

    public List<String> createBackgroundColorList(
            List<Value> values,Context context) {
        List<java.lang.String> colorsList = new ArrayList<>();
        for (Value value : values) {
            if (value.getBackgroundColor() != null) {
                java.lang.String color = value.getBackgroundColor();
                if (!colorsList.contains(color)) {
                    colorsList.add(color);
                }
            }
        }
        //Hardcoded colors for a colorList without colors.
        if (colorsList.size() == 0) {
            colorsList.add("#4d3a4b");
        }
        if (colorsList.size() == 1 && values.size() > 1) {
            colorsList.add("#9c7f9b");
        }
        return colorsList;
    }
}
