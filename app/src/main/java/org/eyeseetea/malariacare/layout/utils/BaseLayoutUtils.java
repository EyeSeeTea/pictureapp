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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Header;
import org.eyeseetea.malariacare.database.model.Option;
import org.eyeseetea.malariacare.database.model.Question;

import java.util.List;

/**
 * Created by Jose on 22/02/2015.
 */
public class BaseLayoutUtils {

    public static final int[] rowBackgrounds =
            {R.drawable.background_even, R.drawable.background_odd};

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
        return rowBackgrounds[index % rowBackgrounds.length];
    }

    public static int getNumberOfQuestionParentsHeader(Header header) {
        int result = 0;

        List<Question> list = header.getQuestions();

        for (Question question : list) {
            if (question.hasChildren()) {
                result = result + 1;
            }
        }

        return result;
    }

    // Used to setup the usual actionbar with the logo and the app name
    public static void setActionBarLogo(ActionBar actionBar) {
        actionBar.setLogo(R.drawable.pictureapp_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.pictureapp_logo);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
    }

    // Used to put the org unit name and the kind of survey instead of the app name
    public static void setActionBarText(ActionBar actionBar, String title, String subtitle) {
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
        int desiredHeight = View.MeasureSpec.makeMeasureSpec(listView.getHeight(),
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

    public static int measureScreenHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.d("measureScreen", metrics.toString());
        return Math.round(metrics.heightPixels * metrics.density);
    }

    /**
     * @param view
     * @param option
     */
    public static void highlightSelection(View view, Option option) {
        Drawable selectedBackground = view.getContext().getResources().getDrawable(
                R.drawable.background_dynamic_clicked_option);
        if (android.os.Build.VERSION.SDK_INT
                > Build.VERSION_CODES.JELLY_BEAN) {    //JELLY_BEAN=API16
            view.setBackground(selectedBackground);
        } else {
            view.setBackgroundDrawable(selectedBackground);
        }

        if (option != null) {
            GradientDrawable bgShape = (GradientDrawable) view.getBackground();
            String backGColor = option.getOptionAttribute() != null
                    ? option.getOptionAttribute().getBackground_colour()
                    : option.getBackground_colour();
            bgShape.setColor(Color.parseColor("#" + backGColor));
            bgShape.setStroke(3, Color.WHITE);
        }

        //the view is a framelayout with a imageview, or a imageview, or a custombutton
        ImageView imageView = null;
        if (view instanceof FrameLayout) {
            FrameLayout f = (FrameLayout) view;
            imageView = (ImageView) f.getChildAt(0);
        } else if (view instanceof ImageView) {
            imageView = (ImageView) view;
        }
        if (imageView != null) {
            imageView.clearColorFilter();
        }
    }

    /**
     * Puts a sort of dark shadow over the given view
     */
    public static void overshadow(FrameLayout view) {
        //FIXME: (API17) setColorFilter for view.getBackground() has no effect...
        view.getBackground().setColorFilter(Color.parseColor("#805a595b"),
                PorterDuff.Mode.SRC_ATOP);
        ImageView imageView = (ImageView) view.getChildAt(0);
        imageView.setColorFilter(Color.parseColor("#805a595b"));

        Drawable bg = view.getBackground();
        if (bg instanceof GradientDrawable) {
            GradientDrawable bgShape = (GradientDrawable) bg;
            bgShape.setStroke(0, 0);
        }
    }

    public static void setDivider(ListView listView) {
    }
}
