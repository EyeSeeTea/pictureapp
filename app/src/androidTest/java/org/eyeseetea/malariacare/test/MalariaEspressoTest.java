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
//import android.app.Activity;
//import android.app.Instrumentation;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.res.AssetManager;
//import android.content.res.Resources;
//import android.database.sqlite.SQLiteDatabase;
//import android.preference.PreferenceManager;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
//import android.support.test.runner.lifecycle.Stage;
//import android.util.Log;
//
//import org.eyeseetea.malariacare.data.database.model.Answer;
//import org.eyeseetea.malariacare.data.database.model.CompositeScore;
//import org.eyeseetea.malariacare.data.database.model.Header;
//import org.eyeseetea.malariacare.data.database.model.Option;
//import org.eyeseetea.malariacare.data.database.model.OrgUnit;
//import org.eyeseetea.malariacare.data.database.model.Program;
//import org.eyeseetea.malariacare.data.database.model.Question;
//import org.eyeseetea.malariacare.data.database.model.Survey;
//import org.eyeseetea.malariacare.data.database.model.Tab;
//import org.eyeseetea.malariacare.data.database.model.User;
//import org.eyeseetea.malariacare.data.database.model.Value;
//import org.eyeseetea.malariacare.data.database.utils.PopulateDB;
//import org.eyeseetea.malariacare.data.database.utils.Session;
//import org.eyeseetea.malariacare.layout.adapters.dashboard.AssessmentUnsentAdapter;
//import org.eyeseetea.malariacare.test.utils.IntentServiceIdlingResource;
//
//import java.util.Collection;
//import java.util.List;
//
//import static android.support.test.espresso.matcher.ViewMatchers.withId;
//
///**
// * Created by arrizabalaga on 25/05/15.
// */
//public class MalariaEspressoTest {
//
//    protected IntentServiceIdlingResource idlingResource;
//    protected Resources res;
//    public static final String DATABASE_NAME="malariacare.db";
//    public static final String DATABASE_FULL_PATH = "/data/data/org.eyeseetea
// .pictureapp/databases/"+DATABASE_NAME;
//
//    public static void init(){
//        cleanAll();
//    }
//
//    public void setup(){
//        res = InstrumentationRegistry.getTargetContext().getResources();
//    }
//
//    public static void cleanAll(){
//        cleanDB();
//        cleanSession();
//        cleanSettings();
//    }
//
//    public static void cleanSession(){
//        Session.setUser(null);
//        Session.setSurvey(null);
//        Session.setAdapterUncompleted(null);
//    }
//
//    public static void cleanDB(){
//        if(!databaseExists()){
//            return;
//        }
//        Question.deleteAll(Question.class);
//        CompositeScore.deleteAll(CompositeScore.class);
//        Option.deleteAll(Option.class);
//        Answer.deleteAll(Answer.class);
//        Header.deleteAll(Header.class);
//        Tab.deleteAll(Tab.class);
//        Program.deleteAll(Program.class);
//        OrgUnit.deleteAll(OrgUnit.class);
//        User.deleteAll(User.class);
//        Value.deleteAll(Value.class);
//        Survey.deleteAll(Survey.class);
//    }
//
//    public static void cleanSettings(){
//        Context activity = InstrumentationRegistry.getInstrumentation().getTargetContext()
// .getApplicationContext();
//        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(activity);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.clear();
//        editor.commit();
//    }
//
//
//    public static boolean databaseExists() {
//        SQLiteDatabase checkDB = null;
//        try {
//            checkDB = SQLiteDatabase.openDatabase(DATABASE_FULL_PATH, null, SQLiteDatabase
// .OPEN_READONLY);
//            checkDB.close();
//            checkDB.releaseReference();
//        } catch (Exception e) {
//            return false;
//        }
//        return checkDB != null;
//    }
//
//    public static void populateData(AssetManager assetManager){
//        try {
//            cleanAll();
//            PopulateDB.populateDummyData();
//            PopulateDB.populateDB(assetManager);
//        }catch(Exception ex){
//            Log.e(".MalariaEspressoTest", ex.getMessage());
//        }
//    }
//
//    public static Survey mockSessionSurvey(int numSurvey, int numProgram, int select){
//        List<Survey> surveys=mockSurveys(numSurvey, numProgram);
//        Survey survey=surveys.get(select);
//        Session.setSurvey(survey);
//        return survey;
//    }
//
//    public static void mockSessionSurvey(int num, int select){
//        mockSessionSurvey(num, 0, select);
//    }
//
//    public static List<Survey> mockSurveys(int numOrgs, int numPrograms){
//        List<OrgUnit> orgUnitList=OrgUnit.find(OrgUnit.class, null, null);
//        List<Program> programList=Program.find(Program.class,null,null);
//        Program program=programList.get(numPrograms);
//        User user =getSafeUser();
//
//        for(int i=0;i<numOrgs;i++){
//            Survey survey=new Survey(orgUnitList.get(i%numOrgs),program,user);
//            survey.save();
//        }
//        List<Survey> surveys= Survey.find(Survey.class, "user=?", user.getId().toString());
//        Session.setAdapterUncompleted(new AssessmentUnsentAdapter(surveys,
// InstrumentationRegistry.getTargetContext()));
//        return surveys;
//    }
//
//    public static List<Survey> mockSurveys(int num){
//        return mockSurveys(num, 0);
//    }
//
//    private static User getSafeUser(){
//        User user=Session.getUser();
//        if(user!=null){
//            return user;
//        }
//        user = new User("user", "user");
//        user.save();
//        Session.setUser(user);
//        return user;
//    }
//
//    protected Activity getActivityInstance(){
//        final Activity[] activity = new Activity[1];
//        Instrumentation instrumentation=InstrumentationRegistry.getInstrumentation();
//        instrumentation.waitForIdleSync();
//        instrumentation.runOnMainSync(new Runnable() {
//            public void run() {
//                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance()
// .getActivitiesInStage(Stage.RESUMED);
//                if (resumedActivities.iterator().hasNext()) {
//                    activity[0] = (Activity) resumedActivities.iterator().next();
//                }
//            }
//        });
//
//        return activity[0];
//    }
//
//    protected static void clearSharedPreferences(){
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
// (InstrumentationRegistry.getTargetContext());
//        sharedPreferences.edit().clear().commit();
//    }
//
//}
//
