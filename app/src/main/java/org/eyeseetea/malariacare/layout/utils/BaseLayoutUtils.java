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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Header;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.exception.ImageNotShowException;
import org.eyeseetea.malariacare.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Jose on 22/02/2015.
 */
public abstract class BaseLayoutUtils {

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

    public static void setActionBarWithOrgUnit(ActionBar actionBar) {
        LayoutUtils.setActionBarLogo(actionBar);
        LayoutUtils.setActionBarText(actionBar, PreferencesState.getInstance().getOrgUnit(),
                PreferencesState.getInstance().getContext().getResources().getString(
                        R.string.malaria_case_based_reporting));
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

    public static void setTabDivider(DashboardActivity dashboardActivity) {
        //No action. This method should be created in the variant.
    }

    public static void setLineBetweenRows(ListView listView) {
        //No action. This method should be created in the variant.
    }

    public static void setRowDivider(ListView listView) {
        //No action. This method should be created in the variant.
    }

    public static void makeImageVisible(String path, ImageView rowImageLabelView) {
        rowImageLabelView.setVisibility(View.VISIBLE);
        putImageInImageView(path,
                rowImageLabelView);
    }


    /**
     * Sets a image from assets path in a imageView
     *
     * @param path      path from assets image
     * @param imageView is the imageView to set the image
     */
    public static void putImageInImageView(String path, ImageView imageView) {
        if (path == null || path.equals("")) {
            return;
        }
        try {
            InputStream inputStream = PreferencesState.getInstance().getContext().getAssets().open(
                    Utils.getInternationalizedString(path));
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            imageView.setImageDrawable(
                    new BitmapDrawable(PreferencesState.getInstance().getContext().getResources(),
                            bmp));
        } catch (IOException e) {
            new ImageNotShowException(e, Utils.getInternationalizedString(path));
        }
    }

    public static void putImageInImageViewDensityHigh(String path, ImageView imageView) {
        if (path == null || path.equals("")) {
            return;
        }
        try {
            InputStream ims = PreferencesState.getInstance().getContext().getAssets().open(path);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inDensity = DisplayMetrics.DENSITY_HIGH;
            Drawable drawable = Drawable.createFromResourceStream(
                    PreferencesState.getInstance().getContext().getResources(), null, ims, null,
                    opts);

            imageView.setImageDrawable(drawable);
            ims.close();
        } catch (IOException e) {
            new ImageNotShowException(e, path);
        }
    }

    /**
     * Sets a Layout Width as 50% of screen pixel
     *
     * @param fixed substract the fixed number from the screenwidth
     */
    public static void setLayoutParamsAs50Percent(View linearLayout, Context context,
            int fixed) {
        LinearLayout.LayoutParams layoutParamsWidth50 = new LinearLayout.LayoutParams(
                ((getScreenWidth(context) - fixed) / 2)
                , ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(layoutParamsWidth50);
    }

    private static int getScreenWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return (metrics.widthPixels);
    }

    public static void setListRowBackgroundColor(View view) {
        //No action. This method should be created in the variant.
    }

    public static void setSurveyActionBar(ActionBar actionBar) {
        //No action. This method should be created in the variant.
    }

    public static void setDashboardActionBar(ActionBar actionBar) {
        //No action. This method should be created in the variant.
    }

    public static void fixRowViewBackground(View row, int position) {
        row.setBackgroundResource(LayoutUtils.calculateBackgrounds(position));
    }
}
