package org.eyeseetea.malariacare.layout.utils;

import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.utils.ConnectivityStatus;

/**
 * Created by idelcano on 01/11/2016.
 */

public class LayoutUtils extends BaseLayoutUtils {
    public static void setActionBar(android.support.v7.app.ActionBar actionBar) {
        setActionBarAppAndUser(actionBar);
    }

    public static void setTabHosts(DashboardActivity dashboardActivity) {
        dashboardActivity.setTabHostsWithImages();
    }

    public static void setTabDivider(DashboardActivity dashboardActivity) {

    }

    public static void setActionBarAppAndUser(ActionBar actionBar) {
        actionBar.setLogo(R.drawable.pictureapp_logo);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setCustomView(R.layout.action_bar_layout);
        ColorDrawable myColor = new ColorDrawable(
                PreferencesState.getInstance().getContext().getResources().getColor(
                        R.color.orange));
        actionBar.setBackgroundDrawable(myColor);
        TextView userName = (TextView) actionBar.getCustomView().findViewById(
                R.id.action_bar_user);
        CredentialsLocalDataSource credentialsLocalDataSource = new CredentialsLocalDataSource();
        Credentials credentials = credentialsLocalDataSource.getOrganisationCredentials();
        userName.setText(credentials.getUsername());
        TextView connection =
                (TextView) actionBar.getCustomView().findViewById(
                        R.id.action_bar_connection_status);
        connection.setText(
                !ConnectivityStatus.isConnected(PreferencesState.getInstance().getContext())
                        ? R.string.action_bar_offline : R.string.action_bar_online);
    }


    public static synchronized void measureListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int itemHeight = listView.getHeight();
        if (itemHeight == 0) {
            return;
        }
        int desiredHeight = View.MeasureSpec.makeMeasureSpec(itemHeight,
                View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredHeight, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight()
                    / 2; //FIXME: measure is not properly measuring (it gives a very high number
            // compared to the screen height measure) so I'm dividing by 2
        }

        setUnsentListHeight(
                totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)));
    }

    public static void setListRowBackgroundColor(View row) {
        int myColor = ContextCompat.getColor(
                PreferencesState.getInstance().getContext(),
                android.R.color.transparent
        );
        row.setBackgroundColor(myColor);
    }


    public static void setDashboardActionBar(ActionBar actionBar) {
        ColorDrawable myColor = new ColorDrawable(
                PreferencesState.getInstance().getContext().getResources().getColor(
                        R.color.orange));
        actionBar.setBackgroundDrawable(myColor);
    }

    public static void setSurveyActionBar(ActionBar actionBar) {
        ColorDrawable myColor = new ColorDrawable(
                PreferencesState.getInstance().getContext().getResources().getColor(
                        R.color.orange));
        actionBar.setBackgroundDrawable(myColor);
    }

    public static void setRowDivider(ListView listView) {
        ColorDrawable myColor = new ColorDrawable(
                PreferencesState.getInstance().getContext().getResources().getColor(
                        R.color.text_first_color)
        );
        listView.setDivider(myColor);
        listView.setDividerHeight(0);
        int padding = (int) PreferencesState.getInstance().getContext().getResources().getDimension(
                R.dimen.dashboard_row_padding);
        listView.setPadding(padding, 0, padding, 0);
    }


    // Given a index, this method return a background color
    public static void fixRowViewBackground(View row, int index) {

    }
}
