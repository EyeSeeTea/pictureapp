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

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Header;
import org.eyeseetea.malariacare.database.model.Question;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.layout.score.ScoreRegister;
import org.eyeseetea.malariacare.utils.Utils;
import org.eyeseetea.malariacare.views.TextCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jose on 22/02/2015.
 */
public class LayoutUtils {

    public static final int [] rowBackgrounds = {R.drawable.background_even, R.drawable.background_odd};

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
}
