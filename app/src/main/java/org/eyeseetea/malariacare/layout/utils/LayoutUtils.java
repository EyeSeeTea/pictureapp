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
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Header;
import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

/**
 * Created by Jose on 22/02/2015.
 */
public class LayoutUtils {

    private final static String TAG=".LayoutUtils";
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



    /**
     * This method try to fix the width by value and screen density size. Its not 1:1 in all the screens
     */
    public static int getPixelsByWidthPercentFixed(int value) {
        if(value==0)
            return 0;
        Log.d(TAG,"Width value"+value);
        DisplayMetrics displayMetrics=PreferencesState.getInstance().getContext().getResources().getDisplayMetrics();
        //fix value by screen size
        int density= Math.round(displayMetrics.widthPixels*
                displayMetrics.density);
        if (density<= Constants.SCREEN_XSMALL) {
            value += value;
            Log.d(TAG,"xsmall Width final value"+value);
        } else if (density<= Constants.SCREEN_MEDIUM) {
            value += value/4;
            Log.d(TAG,"medium Width final value"+value);
        }
        else if (density<= Constants.SCREEN_LARGE) {
            value -= value/4;
            Log.d(TAG,"large Width final value"+value);
        }else if (density<= Constants.SCREEN_XLARGE) {
            value -= value/3;
            Log.d(TAG,"xlarge Width final value"+value);
        }
        else {
            value -= value / 2;
            Log.d(TAG,"+ xxlarge Width final value"+value);
        }
        return calculatePercent(value,Math.round(displayMetrics.widthPixels*displayMetrics.density));
    }

    /**
     * This method try to fix the height by value and screen density size. Its not 1:1 in all the screens
     */
    public static int getPixelsByHeightPercentFixed(int value){
        if(value==0)
            return 0;
        Log.d(TAG,"Height value"+value);
        DisplayMetrics displayMetrics=PreferencesState.getInstance().getContext().getResources().getDisplayMetrics();
        //fix value by screen size
        int density=Math.round(displayMetrics.heightPixels*
                displayMetrics.density);
        if (density<= Constants.SCREEN_XSMALL) {
            value += value;
            Log.d(TAG,"xsmall Height final value"+value);
        } else if (density<= Constants.SCREEN_MEDIUM) {
            value += value/3;
            Log.d(TAG,"medium Height final value"+value);
        }
        else if (density<= Constants.SCREEN_LARGE) {
            value += value/4;
            Log.d(TAG,"large Height final value"+value);
        }else if (density<= Constants.SCREEN_XLARGE) {
            value += value/3;
            Log.d(TAG,"xlarge Height final value"+value);
        }
        else {
            value += value / 2;
            Log.d(TAG,"+ xxlarge Height value"+value);
        }

        Log.d(TAG,"Heigh: "+displayMetrics.heightPixels+" height*density "+(displayMetrics.heightPixels*displayMetrics.density)+" width: "+displayMetrics.widthPixels+" width*density: "+(displayMetrics.widthPixels*displayMetrics.density));
        Log.d(TAG,"dpiy  "+displayMetrics.ydpi+" dpix "+displayMetrics.xdpi);
        Log.d(TAG,"densitydpi:  "+displayMetrics.densityDpi+" density: "+displayMetrics.density);

        return calculatePercent(value,Math.round(displayMetrics.heightPixels*displayMetrics.density));
    }

    private static int calculatePercent(int percent, int total) {
        int pixels=(total*percent)/100;
        return pixels;
    }
}
