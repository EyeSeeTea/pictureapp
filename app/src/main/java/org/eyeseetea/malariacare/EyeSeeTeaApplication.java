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
import android.content.Context;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;

import com.crashlytics.android.Crashlytics;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.index.Index;

import org.eyeseetea.malariacare.database.PostMigration;
import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.Match$Table;
import org.eyeseetea.malariacare.database.model.QuestionOption;
import org.eyeseetea.malariacare.database.model.QuestionOption$Table;
import org.eyeseetea.malariacare.database.model.QuestionRelation;
import org.eyeseetea.malariacare.database.model.QuestionRelation$Table;
import org.eyeseetea.malariacare.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.database.model.QuestionThreshold$Table;
import org.eyeseetea.malariacare.database.model.Value;
import org.eyeseetea.malariacare.database.model.Value$Table;
import org.eyeseetea.malariacare.database.utils.LocationMemory;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.eyeseetea.malariacare.phonemetadata.PhoneMetaData;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.malariacare.utils.Utils;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;

import io.fabric.sdk.android.Fabric;

/**
 * Created by nacho on 04/08/15.
 */
public class EyeSeeTeaApplication extends Dhis2Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        PreferencesState.getInstance().init(getApplicationContext());
        LocationMemory.getInstance().init(getApplicationContext());

        //Set the Phone metadata
        PhoneMetaData phoneMetaData=this.getPhoneMetadata();
        Session.setPhoneMetaData(phoneMetaData);

        FlowManager.init(this, "_EyeSeeTeaDB");
        createDBIndexes();
        PostMigration.launchPostMigration();
    }

    private void createDBIndexes(){
        new Index<QuestionOption>(Constants.QUESTION_OPTION_QUESTION_IDX).on(QuestionOption.class, QuestionOption$Table.ID_QUESTION).enable();
        new Index<QuestionOption>(Constants.QUESTION_OPTION_MATCH_IDX).on(QuestionOption.class, QuestionOption$Table.ID_MATCH).enable();

        new Index<QuestionRelation>(Constants.QUESTION_RELATION_OPERATION_IDX).on(QuestionRelation.class, QuestionRelation$Table.OPERATION).enable();
        new Index<QuestionRelation>(Constants.QUESTION_RELATION_QUESTION_IDX).on(QuestionRelation.class, QuestionRelation$Table.ID_QUESTION).enable();

        new Index<Match>(Constants.MATCH_QUESTION_RELATION_IDX).on(Match.class, Match$Table.ID_QUESTION_RELATION).enable();

        new Index<QuestionThreshold>(Constants.QUESTION_THRESHOLDS_QUESTION_IDX).on(QuestionThreshold.class, QuestionThreshold$Table.ID_QUESTION).enable();

        new Index<Value>(Constants.VALUE_IDX).on(Value.class, Value$Table.ID_SURVEY).enable();
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
        phoneMetaData.setBuild_number(Utils.getCommitHash(getApplicationContext()));

        return phoneMetaData;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        FlowManager.destroy();
    }

    @Override
    public Class<? extends Activity> getMainActivity() {
        return new DashboardActivity().getClass();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }



}
