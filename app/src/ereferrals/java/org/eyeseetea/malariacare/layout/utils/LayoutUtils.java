package org.eyeseetea.malariacare.layout.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.entity.Credentials;

/**
 * Created by idelcano on 01/11/2016.
 */

public class LayoutUtils extends BaseLayoutUtils {
    public static void setActionBar(android.support.v7.app.ActionBar actionBar) {
        setActionBarAppAndUser(actionBar);
    }

    public static void setTabHosts(DashboardActivity dashboardActivity) {
        dashboardActivity.setTabHostsWithText();
    }

    public static void setTabDivider(DashboardActivity dashboardActivity) {
        dashboardActivity.setTabDivider();
    }

    public static void setActionBarAppAndUser(ActionBar actionBar) {
        Context context = PreferencesState.getInstance().getContext();
        actionBar.setLogo(R.drawable.pictureapp_logo);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        int color = ContextCompat.getColor(context, R.color.text_first_color);
        String colorString = String.format("%X", color).substring(2);
        Spanned spannedTitle = Html.fromHtml(
                String.format("<font color=\"#%s\" size=\"10\"><b>%s</b></font>", colorString,
                        context.getString(R.string.malaria_case_based_reporting)));
        color = ContextCompat.getColor(context, R.color.text_second_color);
        colorString = String.format("%X", color).substring(2);


        CredentialsLocalDataSource credentialsLocalDataSource = new CredentialsLocalDataSource();

        Credentials credentials = credentialsLocalDataSource.getOrganisationCredentials();
        String userName = credentials.getUsername();

        String volunteer = context.getString(R.string.volunteer_label);

        Spanned spannedSubTitle = Html.fromHtml(
                String.format("<font color=\"#%s\"><b>%s</b></font>", colorString,
                        volunteer + " " + userName + ""));
        actionBar.setCustomView(R.layout.custom_action_bar);
        TextView title = (TextView) actionBar.getCustomView().findViewById(
                R.id.action_bar_multititle_title);
        title.setText(spannedTitle);
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/" + context.getString(R.string.light_font));
        title.setTypeface(tf);
        TextView subtitle = (TextView) actionBar.getCustomView().findViewById(
                R.id.action_bar_multititle_subtitle);
        subtitle.setText(spannedSubTitle);
        tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/" + context.getString(R.string.light_font));
        subtitle.setTypeface(tf);

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
                R.color.rowBackgroundColor
        );
        row.setBackgroundColor(myColor);
    }


    public static void setDashboardActionBar(ActionBar actionBar) {
        ColorDrawable myColor = new ColorDrawable(
                PreferencesState.getInstance().getContext().getResources().getColor(
                        R.color.actionbar_background));
        actionBar.setBackgroundDrawable(myColor);
    }

    public static void setSurveyActionBar(ActionBar actionBar) {
        ColorDrawable myColor = new ColorDrawable(
                PreferencesState.getInstance().getContext().getResources().getColor(
                        R.color.myanmar_orange));
        actionBar.setBackgroundDrawable(myColor);
    }

    public static void setRowDivider(ListView listView) {
        ColorDrawable myColor = new ColorDrawable(
                PreferencesState.getInstance().getContext().getResources().getColor(
                        R.color.headerColor)
        );
        listView.setDivider(myColor);
        listView.setDividerHeight(0);
        int padding = (int) PreferencesState.getInstance().getContext().getResources().getDimension(
                R.dimen.dashboard_row_padding);
        listView.setPadding(padding, 0, padding, 0);
    }


    // Given a index, this method return a background color
    public static void fixRowViewBackground(View row, int index) {
        if (index < 1) {
            row.findViewById(R.id.dotted_line).setVisibility(View.GONE);
        }
    }
}
