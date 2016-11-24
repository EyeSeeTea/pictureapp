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
import android.widget.ProgressBar;
import android.widget.TextView;

import org.eyeseetea.malariacare.R;

/**
 * Created by rhardjono on 08/11/2016
 */
public class ProgressUtils {

    public static void updateProgressBarStatus(View view, int currentPage, int totalPages) {
        ProgressBar progressView = (ProgressBar) view.findViewById(R.id.dynamic_progress);
        TextView progressText = (TextView) view.findViewById(R.id.dynamic_progress_text);
        progressView.setMax(totalPages);
        progressView.setProgress(currentPage + 1);
        progressText.setText(
                getLocaleProgressStatus(progressView.getContext(), progressView.getProgress(),
                        progressView.getMax()));
    }

    public static void setProgressBarText(View view, String newText) {
        ((TextView) view.findViewById(R.id.dynamic_progress_text)).setText(newText);
    }

    private static String getLocaleProgressStatus(Context context, int currentPage,
            int totalPages) {
        String current = context.getResources().getString(
                context.getResources().getIdentifier("number_" + currentPage, "string",
                        context.getPackageName()));
        String total = context.getResources().getString(
                context.getResources().getIdentifier("number_" + totalPages, "string",
                        context.getPackageName()));
        return current.concat("/").concat(total);
    }
}
