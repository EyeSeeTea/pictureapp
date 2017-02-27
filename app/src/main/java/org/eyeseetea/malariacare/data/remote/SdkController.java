/*
 * Copyright (c) 2016.
 *
 * This file is part of QA App.
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

package org.eyeseetea.malariacare.data.remote;

import com.fasterxml.jackson.databind.JsonNode;
import com.raizlabs.android.dbflow.sql.language.Delete;

import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.AttributeFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.AttributeValueFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.DataElementFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.EventFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.FailedItemFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OptionFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OptionSetFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OrganisationUnitFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageDataElementFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramStageSectionFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.TrackedEntityDataValueFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.UserAccountFlow;

import java.util.List;

/**
 * Created by idelcano on 09/11/2016.
 */

public abstract class SdkController {

    public final static String TAG = ".sdkController";
    public final static Class[] MANDATORY_METADATA_TABLES = {
            AttributeFlow.class,
            DataElementFlow.class,
            //DataElementAttributeValueFlow.class,
            OptionFlow.class,
            OptionSetFlow.class,
            UserAccountFlow.class,
            OrganisationUnitFlow.class,
            //OrganisationUnitProgramRelationshipFlow.class,
            ProgramStageFlow.class,
            ProgramStageDataElementFlow.class,
            ProgramStageSectionFlow.class
    };

    public static void postProgress(String msg) {
        /*
        ProgressActivity.step(msg);
        */
    }

    public static void postException(Exception ex) {
        /*
        ProgressActivity.cancellPull("error",ex.getMessage());
        */
    }

    public static void postFinish() {
        /*
        //Fixme maybe it is not the best place to reload the logged user.(Without reload the user
        // after pull, the user had diferent id and application crash).
        User user = User.getLoggedUser();
        Session.setUser(user);


        ProgressActivity.postFinish(); //new way in malariapp
        //in pictureapp
        Dhis2Application.getEventBus().post(new SyncProgressStatus());
        */
    }

    public static boolean finishPullJob() {
        /*
        if (JobExecutor.isJobRunning(IPullController.job.getJobId())) {
            Log.d(TAG, "Job " + IPullController.job.getJobId() + " is running");
            IPullController.job.cancel(true);
            try {
                try {JobExecutor.getInstance().dequeueRunningJob(IPullController.job);} catch
                (Exception e) {e.printStackTrace();}
                IPullController.job.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                return true;
            }
        }
        */
        return false;
    }

    public static List<EventExtended> getEventsFromEventsWrapper(JsonNode jsonNode) {
        /*
        List<EventExtended> eventExtendeds = new ArrayList<>();
        List<EventFlow> eventFlows = EventsWrapper.getEvents(jsonNode);
        for (EventFlow eventFlow:eventFlows){
            eventExtendeds.add(new EventExtended(eventFlow));
        }
        return eventExtendeds;
        */
        return null;
    }


    public static void wipeData() {
        Delete.tables(
                EventFlow.class,
                TrackedEntityDataValueFlow.class,
                FailedItemFlow.class,
                AttributeValueFlow.class
        );
    }

    public static String getDhisDatabaseName() {
        return DbDhis.NAME;
    }

}
