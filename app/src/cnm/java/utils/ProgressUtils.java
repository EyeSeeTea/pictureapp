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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.eyeseetea.malariacare.R;

import java.util.ArrayList;

/**
 * Created by rhardjono on 08/11/2016
 */
public class ProgressUtils {

    public static void updateProgressBarStatus(View view, int currentPage, int totalPages) {
        ArrayList<View> progressViews = new ArrayList<>();
        LinearLayout progressContainer = (LinearLayout) view.findViewById(R.id.dynamic_progress);
        setTotalPages(progressContainer, totalPages);
        setProgress(progressViews, currentPage + 1, progressContainer, totalPages);
    }

    private static void setProgress(ArrayList<View> progressViews, int currentPage,
            LinearLayout progressContainer, int totalPages) {
        Context context = progressContainer.getContext();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;

        for (int i = 0; i < totalPages; i++) {
            int backgroundColor = i < currentPage ? R.color.tab_pressed_background
                    : R.color.tab_unpressed_background;
            View progress = new View(context);
            progress.setBackgroundColor(
                    context.getResources().getColor(backgroundColor));
            progress.setLayoutParams(lp);
            progressContainer.addView(progress);
            if (i < totalPages - 1) {
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.tab_line);
                progressContainer.addView(imageView);
                imageView.setBackgroundColor(
                        context.getResources().getColor(backgroundColor));
                imageView.setPadding(0, 0,
                        context.getResources().getDimensionPixelSize(
                                R.dimen.padding_right_progress),
                        0);
            }
        }
    }

    private static void setTotalPages(LinearLayout progressContainer,
            int totalPages) {
        progressContainer.setWeightSum(totalPages);
    }

    public static void setProgressBarText(View view, String newText) {
    }
}
