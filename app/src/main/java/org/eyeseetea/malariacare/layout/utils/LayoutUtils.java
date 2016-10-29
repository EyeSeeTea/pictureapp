/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App.
 *
 *  QIS Surveillance App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.layout.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Header;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Jose on 22/02/2015.
 */
public class LayoutUtils {

    public static final int [] rowBackgrounds = {R.drawable.background_even, R.drawable.background_odd};

    /**
     * Variable to store the Unsent surveys list height
     */
    private static int unsentListHeight;


    public static synchronized int getUnsentListHeight() {
        return unsentListHeight;
    }

    public static synchronized void setUnsentListHeight(int unsentHeight) {
        unsentListHeight = unsentHeight;
    }

    // Given a index, this method return a background color
    public static int calculateBackgrounds(int index) {
        return LayoutUtils.rowBackgrounds[index % LayoutUtils.rowBackgrounds.length];
    }

    public static int getNumberOfQuestionParentsHeader(Header header) {
        int result = 0;

        List<Question> list =  header.getQuestions();

        for (Question question : list)
            if (question.hasChildren())
                result = result + 1;

        return result;
    }


    public static void setActionBarAppAndUser(ActionBar actionBar) {
        actionBar.setDisplayShowCustomEnabled(true);
        Context context = PreferencesState.getInstance().getContext();
        int color = ContextCompat.getColor(context, R.color.text_first_color);
        String colorString = String.format("%X", color).substring(2);
        Spanned spannedTitle = Html.fromHtml(String.format("<font color=\"#%s\" size=\"10\"><b>%s</b></font>", colorString , context.getString(R.string.app_name)));
        color = ContextCompat.getColor(context, R.color.text_first_color);
        colorString = String.format("%X", color).substring(2);
        Spanned spannedSubTitle = Html.fromHtml(String.format("<font color=\"#%s\"><b>%s</b></font>", colorString , "Volunteer: "+ User.getLoggedUser().getName()+""));
        actionBar.setCustomView(R.layout.custom_action_bar);
        TextView title =(TextView) actionBar.getCustomView().findViewById(R.id.action_bar_multititle_title);
        title.setText(spannedTitle);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/" +  context.getString(R.string.light_font));
        title.setTypeface(tf);
        TextView subtitle =(TextView) actionBar.getCustomView().findViewById(R.id.action_bar_multititle_subtitle);
        subtitle.setText(spannedSubTitle);
         tf = Typeface.createFromAsset(context.getAssets(), "fonts/" +  context.getString(R.string.light_font));
        subtitle.setTypeface(tf);

    }

    // Used to setup the usual actionbar with the logo and the app name
    public static void setActionBarLogo(ActionBar actionBar){
        actionBar.setLogo(R.drawable.pictureapp_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.pictureapp_logo);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }

    // Used to put the org unit name and the kind of survey instead of the app name
    public static void setActionBarText(ActionBar actionBar, String title, String subtitle){
        actionBar.setDisplayUseLogoEnabled(false);
        // Uncomment in case of we want the logo out
        // actionBar.setLogo(null);
        // actionBar.setIcon(null);
        actionBar.setTitle(title);
        actionBar.setSubtitle(subtitle);
    }

    public static synchronized void measureListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        int desiredHeight = View.MeasureSpec.makeMeasureSpec(listView.getHeight(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredHeight, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight()/2; //FIXME: measure is not properly measuring (it gives a very high number compared to the screen height measure) so I'm dividing by 2
        }

        setUnsentListHeight(totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)));
    }

    public static int measureScreenHeight(Activity activity){
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.d("measureScreen", metrics.toString());
        return Math.round(metrics.heightPixels*metrics.density);
    }
}
