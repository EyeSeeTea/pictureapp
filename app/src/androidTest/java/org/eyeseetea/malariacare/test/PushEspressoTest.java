///*
// * Copyright (c) 2015.
// *
// * This file is part of QIS Surveillance App.
// *
// *  QIS Surveillance App is free software: you can redistribute it and/or modify
// *  it under the terms of the GNU General Public License as published by
// *  the Free Software Foundation, either version 3 of the License, or
// *  (at your option) any later version.
// *
// *  QIS Surveillance App is distributed in the hope that it will be useful,
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
//import android.app.Instrumentation;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.espresso.Espresso;
//import android.support.test.espresso.ViewInteraction;
//import android.support.test.espresso.intent.rule.IntentsTestRule;
//import android.support.test.runner.AndroidJUnit4;
//import android.util.Log;
//
//import org.eyeseetea.malariacare.DashboardActivity;
//import org.eyeseetea.malariacare.R;
//import org.eyeseetea.malariacare.SurveyActivity;
//import org.eyeseetea.malariacare.data.database.model.Option;
//import org.eyeseetea.malariacare.data.database.model.Question;
//import org.eyeseetea.malariacare.data.database.model.Survey;
//import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
//import org.eyeseetea.malariacare.fragments.DashboardUnsentFragment;
//import org.eyeseetea.malariacare.services.SurveyService;
//import org.eyeseetea.malariacare.test.utils.IntentServiceIdlingResource;
//import org.eyeseetea.malariacare.utils.Constants;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.util.List;
//
//import static android.support.test.espresso.Espresso.onData;
//import static android.support.test.espresso.Espresso.onView;
//import static android.support.test.espresso.action.ViewActions.click;
//import static android.support.test.espresso.action.ViewActions.longClick;
//import static android.support.test.espresso.action.ViewActions.swipeRight;
//import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
//import static android.support.test.espresso.assertion.ViewAssertions.matches;
//import static android.support.test.espresso.intent.Intents.intended;
//import static android.support.test.espresso.intent.matcher.IntentMatchers.anyIntent;
//import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
//import static android.support.test.espresso.matcher.ViewMatchers.withChild;
//import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
//import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
//import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static junit.framework.Assert.assertEquals;
//import static org.eyeseetea.malariacare.test.utils.MalariaEspressoActions.waitId;
//import static org.eyeseetea.malariacare.test.utils.TextCardScaleMatcher.hasTextCardScale;
//import static org.hamcrest.Matchers.allOf;
//import static org.hamcrest.Matchers.endsWith;
//import static org.hamcrest.Matchers.instanceOf;
//import static org.hamcrest.Matchers.is;
//
///**
// *
// */
//@RunWith(AndroidJUnit4.class)
//public class PushEspressoTest extends MalariaEspressoTest{
//
//    private static String TAG=".PushEspressoTest";
//
//    @Rule
//    public IntentsTestRule<DashboardActivity> mActivityRule = new IntentsTestRule<>(
//            DashboardActivity.class);
//
//    @BeforeClass
//    public static void init(){
//        populateData(InstrumentationRegistry.getTargetContext().getAssets());
////        mockSessionSurvey(_EXPECTED_SURVEYS, 1, 0);
//    }
//
//    @Before
//    public void registerIntentServiceIdlingResource(){
//        Log.i(TAG,"---BEFORE---");
//        super.setup();
//        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
//        idlingResource = new IntentServiceIdlingResource(instrumentation.getTargetContext(),
// SurveyService.class);
//        Espresso.registerIdlingResources(idlingResource);
//    }
//
//    @After
//    public void unregisterIntentServiceIdlingResource(){
//        Log.i(TAG,"---AFTER---");
//        Espresso.unregisterIdlingResources(idlingResource);
//        unregisterSurveyReceiver();
//        getActivityInstance().finish();
//    }
//
//    @Test
//    public void form_views() {
//        Log.i(TAG, "------form_views------");
//        onView(isRoot()).perform(waitId(R.id.plusButton, 3000));
//    }
//
//    @Test
//    public void org_unit_ko_push_ko() {
//        Log.i(TAG, "------org_unit_ko_push_ko------");
//        //GIVEN
//        onView(withId(R.id.plusButton)).perform(click());
//        SurveyActivity surveyActivity=(SurveyActivity)getActivityInstance();
//
//        //WHEN
//        chooseOptionWithText(surveyActivity, "No");
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        //THEN
//        onView(withText(surveyActivity.getString(android.R.string.ok))).check(matches
// (isDisplayed())).perform(click());
//
//        //WHEN
//        onRowWithSurvey(Survey.getUnsentSurveys(1).get(0)).perform(longClick());
//        onView(withText(surveyActivity.getString(android.R.string.ok))).check(matches
// (isDisplayed())).perform(click());
//
//        //THEN
//        onView(withText(surveyActivity.getString(R.string.dialog_error_push_no_org_unit)))
// .check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void org_unit_ok_location_ko_push_ko() {
//        Log.i(TAG, "------org_unit_ok_location_ko_push_ko------");
//        //GIVEN
//        PreferencesState.getInstance().setOrgUnit("KH_Cambodia");
//        onView(withId(R.id.plusButton)).perform(click());
//        SurveyActivity surveyActivity=(SurveyActivity)getActivityInstance();
//
//        //WHEN
//        chooseOptionWithText(surveyActivity, "No");
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        //THEN
//        onView(withText(surveyActivity.getString(android.R.string.ok))).check(matches
// (isDisplayed())).perform(click());
//
//        //WHEN
//        onRowWithSurvey(Survey.getUnsentSurveys(1).get(0)).perform(longClick());
//        onView(withText(surveyActivity.getString(android.R.string.ok))).check(matches
// (isDisplayed())).perform(click());
//
//        //THEN
//        onView(withText(surveyActivity.getString(R.string.dialog_error_push_no_location)))
// .check(matches(isDisplayed()));
//    }
//
//    private void chooseOptionWithText(SurveyActivity activity,String text){
//        Option noOption=activity.findOptionByText(text);
//        onView(withTagValue(is((Object)noOption))).perform(click());
//    }
//
//    private ViewInteraction onRowWithSurvey(Survey survey){
//        return onView(allOf(withClassName(endsWith("TableRow")),withTagValue(is((Object)survey
// .getId()))));
//    }
//
//    private void unregisterSurveyReceiver(){
//        try{
//            DashboardActivity dashboardActivity =(DashboardActivity)getActivityInstance();
//            DashboardUnsentFragment dashboardUnsentFragment =(DashboardUnsentFragment)
// dashboardActivity.getFragmentManager().findFragmentById(R.id.dashboard_details_fragment);
//            dashboardUnsentFragment.unregisterFragmentReceiver();
//        }catch(Exception ex){
//            Log.e(TAG,"unregisterSurveyReceiver(): "+ex.getMessage());
//        }
//    }
//
//}