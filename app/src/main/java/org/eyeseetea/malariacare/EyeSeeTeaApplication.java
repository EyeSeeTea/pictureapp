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

package org.eyeseetea.malariacare;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.config.EyeSeeTeaGeneratedDatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.utils.Permissions;

import io.fabric.sdk.android.Fabric;

/**
 * Created by nacho on 04/08/15.
 */
public class EyeSeeTeaApplication extends Application {

    private static final String TAG = ".EyeSeeTeaApplication";
    public static Permissions permissions;

    private static boolean isAppWentToBg = false;

    private static boolean isWindowFocused = false;

    private static boolean isBackPressed = false;

    private static EyeSeeTeaApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mInstance = this;
        Fabric.with(this, new Crashlytics());
        PreferencesState.getInstance().init(getApplicationContext());
        FlowConfig flowConfig = new FlowConfig
                .Builder(this)
                .addDatabaseHolder(EyeSeeTeaGeneratedDatabaseHolder.class)
                .build();
        FlowManager.init(flowConfig);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        FlowManager.destroy();
    }

    //// FIXME: 28/12/16
    //@Override
    public Class<? extends Activity> getMainActivity() {
        return DashboardActivity.class;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static EyeSeeTeaApplication getInstance() {
        return mInstance;
    }

    public boolean isAppWentToBg() {
        //// FIXME: 30/05/2017 remove this line and uncomment the next one to restore the disable login feature
        return false;
        //return isAppWentToBg;
    }

    public void setIsAppWentToBg(boolean isAppWentToBg) {
        EyeSeeTeaApplication.isAppWentToBg = isAppWentToBg;
    }

    public boolean isWindowFocused() {
        return isWindowFocused;
    }

    public void setIsWindowFocused(boolean isWindowFocused) {
        EyeSeeTeaApplication.isWindowFocused = isWindowFocused;
    }

    public boolean isBackPressed() {
        return isBackPressed;
    }

    public void setIsBackPressed(boolean isBackPressed) {
        EyeSeeTeaApplication.isBackPressed = isBackPressed;
    }
}
