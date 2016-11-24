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

package utils;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.utils.Session;

/**
 * Created by rhardjono on 08/11/2016
 */
public class ProgressUtils {

    private static int DEFAULT_MAX_TOTALPAGES = 9;

    public static void updateProgressBarStatus(View view, int currentPage, int totalPages) {

        LinearLayout pager_indicator = (LinearLayout) view.findViewById(R.id.page_progress);

        int maxTotalPages = (Session.getMaxTotalQuestions() > 0 ? Session.getMaxTotalQuestions()
                : DEFAULT_MAX_TOTALPAGES);
        float dotsDistribution = 1.0f / maxTotalPages;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, dotsDistribution
        );

        ImageView[] dots = new ImageView[totalPages];
        for (int i = 0; i < totalPages; i++) {
            dots[i] = new ImageView(view.getContext());
            dots[i].setScaleType(ImageView.ScaleType.FIT_START);
            dots[i].setPadding(15, 15, 0, 0);
            if (i <= currentPage) {
                dots[i].setImageDrawable(
                        ResourcesCompat.getDrawable(view.getContext().getResources(),
                                R.drawable.page_indicator_completed, null));
            } else {
                dots[i].setImageDrawable(
                        ResourcesCompat.getDrawable(view.getContext().getResources(),
                                R.drawable.page_indicator_pending, null));
            }
            pager_indicator.addView(dots[i], layoutParams);
        }
    }

    public static void setProgressBarText(View view, String newText) {

    }


    public String getLocaleProgressStatus(Context context, int currentPage, int totalPages) {
        return null;
    }

}
