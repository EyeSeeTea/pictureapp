///*
// * Copyright (c) 2015.
// *
// * This file is part of QIS Surveillance App App.
// *
// *  QIS Surveillance App App is free software: you can redistribute it and/or modify
// *  it under the terms of the GNU General Public License as published by
// *  the Free Software Foundation, either version 3 of the License, or
// *  (at your option) any later version.
// *
// *  QIS Surveillance App App is distributed in the hope that it will be useful,
// *  but WITHOUT ANY WARRANTY; without even the implied warranty of
// *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *  GNU General Public License for more details.
// *
// *  You should have received a copy of the GNU General Public License
// *  along with QIS Surveillance App.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package org.eyeseetea.malariacare.test;
//
//import android.support.test.espresso.intent.rule.IntentsTestRule;
//import android.support.test.runner.AndroidJUnit4;
//import android.test.suitebuilder.annotation.LargeTest;
//import android.util.Log;
//
//import junit.framework.Assert;
//
//import org.eyeseetea.malariacare.R;
//import org.eyeseetea.malariacare.SettingsActivity;
//import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
//import org.eyeseetea.malariacare.utils.Constants;
//import org.junit.BeforeClass;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static android.support.test.espresso.Espresso.onView;
//import static android.support.test.espresso.Espresso.pressBack;
//import static android.support.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.assertion.ViewAssertions.matches;
//import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static junit.framework.Assert.assertEquals;
//import static org.hamcrest.Matchers.allOf;
//import static org.hamcrest.Matchers.is;
//import static org.hamcrest.Matchers.not;
//
//
///**
// * Espresso tests for the survey that contains scores, compositeScores
// */
//@RunWith(AndroidJUnit4.class)
//@LargeTest
//public class SettingsEspressoTest extends MalariaEspressoTest{
//
//    private static String TAG=".SettingsEspressoTest";
//
//    @Rule
//    public IntentsTestRule<SettingsActivity> mActivityRule = new IntentsTestRule<>(
//            SettingsActivity.class);
//
//    @BeforeClass
//    public static void init(){
//        clearSharedPreferences();
//    }
//
//    @Test
//    public void form_views() {
//        Log.i(TAG,"------form_views------");
//        //THEN
//        onView(withText("Show num/dems")).check(matches(isDisplayed()));
//        onView(withText("Customize fonts?")).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void change_font(){
//        Log.i(TAG, "------change_font------");
//
//        //WHEN
//        whenFontSizeChange(3);
//        pressBack();
//
//        //THEN
//        Assert.assertEquals(Constants.FONTS_LARGE, PreferencesState.getInstance().getScale());
//    }
//
//    @Test
//    public void change_num_den(){
//        Log.i(TAG, "------change_num_den------");
//
//        //WHEN
//        whenToggleShowHideNumDem();
//        pressBack();
//
//        //THEN
//        Assert.assertEquals(true, PreferencesState.getInstance().isShowNumDen());
//    }
//
//    /**
//     * Change font size
//     * @param num font size in a discrete int scale [0: xsmall - 1: small - 2: medium - 3:
// large - 4: xlarge]
//     */
//    private void whenFontSizeChange(int num) {
//        SettingsActivity settingsActivity=(SettingsActivity)getActivityInstance();
//        onView(withText(settingsActivity.getString(R.string.settings_checkbox_customize_fonts))
// ).perform(click());
//        onView(withText(settingsActivity.getString(R.string.settings_list_font_sizes))).perform
// (click());
//        onView(withText((settingsActivity.getResources().getStringArray(R.array
// .settings_array_titles_font_sizes))[num])).perform(click());
//    }
//
//    /**
//     * Change show/hide num/dem preference
//     */
//    private void whenToggleShowHideNumDem() {
//        SettingsActivity settingsActivity=(SettingsActivity)getActivityInstance();
//        onView(withText(settingsActivity.getString(R.string.settings_checkbox_show_num_dems)))
// .perform(click());
//    }
//
//}