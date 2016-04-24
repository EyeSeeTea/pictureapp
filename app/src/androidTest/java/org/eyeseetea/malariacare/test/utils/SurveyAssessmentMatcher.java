/*
 * Copyright (c) 2015.
 *
 * This file is part of QIS Surveillance App App.
 *
 *  QIS Surveillance App App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  QIS Surveillance App App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.test.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.eyeseetea.malariacare.views.TextCard;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static com.google.android.apps.common.testing.deps.guava.base.Preconditions.checkNotNull;

/**
 * Created by arrizabalaga on 25/05/15.
 */
public class SurveyAssessmentMatcher extends TypeSafeMatcher<View> {
    private final String facility;
    private final String surveyType;

    private SurveyAssessmentMatcher(String facility, String surveyType) {
        this.facility = checkNotNull(facility);
        this.surveyType = checkNotNull(surveyType);
    }

    @Override
    public boolean matchesSafely(View view) {
        if (!(view instanceof EditText)) {
            return false;
        }
        TextCard facility = (TextCard) ((ViewGroup) view).getChildAt(0);
        TextCard surveyType = (TextCard) ((ViewGroup) view).getChildAt(0);
        return (facility.getText().equals(this.facility) && surveyType.getText().equals(this.surveyType));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with error: " + facility + " - " + surveyType);
    }

    public static Matcher<? super View> hasSurvey(String facility, String surveyType) {
        return new SurveyAssessmentMatcher(facility, surveyType);
    }
}