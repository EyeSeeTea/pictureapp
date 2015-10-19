/*
 * Copyright (c) 2015.
 *
 * This file is part of Health Network QIS App.
 *
 *  Health Network QIS App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Health Network QIS App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;

import com.crashlytics.android.Crashlytics;

import org.eyeseetea.malariacare.database.model.CompositeScore;
import org.eyeseetea.malariacare.database.model.QuestionRelation;
import org.eyeseetea.malariacare.database.model.Score;
import org.eyeseetea.malariacare.database.model.Survey;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.utils.LocationMemory;
import org.eyeseetea.malariacare.database.utils.Session;

import org.eyeseetea.malariacare.phonemetadata.PhoneMetaData;


import io.fabric.sdk.android.Fabric;

/**
 * Created by nacho on 04/08/15.
 */
public class EyeSeeTeaApplication extends com.orm.SugarApp {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        LocationMemory.getInstance().init(getApplicationContext());

        //Set the Phone metadata
        PhoneMetaData phoneMetaData=this.getPhoneMetadata();
        Session.setPhoneMetaData(phoneMetaData);


        // Not previously populated tables

        CompositeScore.saveInTx();
        QuestionRelation.saveInTx();
        Score.saveInTx();
        Survey.saveInTx();
        User.saveInTx();
        Value.saveInTx();
    }

    PhoneMetaData getPhoneMetadata(){
        PhoneMetaData phoneMetaData=new PhoneMetaData();
        TelephonyManager phoneManagerMetaData=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String imei = phoneManagerMetaData.getDeviceId();
        String phone = phoneManagerMetaData.getLine1Number();
        String serial = phoneManagerMetaData.getSimSerialNumber();
        phoneMetaData.setImei(imei);
        phoneMetaData.setPhone_number(phone);
        phoneMetaData.setPhone_serial(serial);
        return phoneMetaData;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
