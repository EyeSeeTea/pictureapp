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

package org.eyeseetea.malariacare.database.iomodules.dhis.importer;

import android.content.Context;
import android.util.Log;

import com.raizlabs.android.dbflow.runtime.transaction.process.ProcessModelInfo;
import com.raizlabs.android.dbflow.runtime.transaction.process.SaveModelTransaction;
import com.squareup.otto.Subscribe;

import org.eyeseetea.malariacare.ProgressActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.models.DataValueExtended;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.models.EventExtended;
import org.eyeseetea.malariacare.database.iomodules.dhis.importer.models.OrganisationUnitExtended;
import org.eyeseetea.malariacare.database.model.Program;
import org.eyeseetea.malariacare.database.model.User;
import org.eyeseetea.malariacare.database.utils.PopulateDB;
import org.eyeseetea.malariacare.database.utils.PreferencesState;
import org.eyeseetea.malariacare.database.utils.Session;
import org.hisp.dhis.android.sdk.controllers.DhisService;
import org.hisp.dhis.android.sdk.controllers.LoadingController;
import org.hisp.dhis.android.sdk.controllers.metadata.MetaDataController;
import org.hisp.dhis.android.sdk.controllers.tracker.TrackerController;
import org.hisp.dhis.android.sdk.job.Job;
import org.hisp.dhis.android.sdk.job.JobExecutor;
import org.hisp.dhis.android.sdk.job.NetworkJob;
import org.hisp.dhis.android.sdk.persistence.Dhis2Application;
import org.hisp.dhis.android.sdk.persistence.models.DataValue;
import org.hisp.dhis.android.sdk.persistence.models.Event;
import org.hisp.dhis.android.sdk.persistence.models.OrganisationUnit;
import org.hisp.dhis.android.sdk.persistence.preferences.ResourceType;
import org.hisp.dhis.android.sdk.utils.api.ProgramType;

import java.util.List;

/**
 * A static controller that orchestrate the pull process
 * Created by arrizabalaga on 4/11/15.
 */
public class PullController {
    public static final int MAX_EVENTS_X_ORGUNIT_PROGRAM = 4800;
    private final String TAG = ".PullController";

    private static PullController instance;

    private static Job job;
    /**
     * Context required to i18n error messages while pulling
     */
    private Context context;

    /**
     * Constructs and register this pull controller to the event bus
     */
    PullController() {
    }

    private void register() {
        try {
            Dhis2Application.bus.register(this);
        } catch (Exception e) {
            unregister();
            Dhis2Application.bus.register(this);
        }
    }

    /**
     * Unregister pull controller from bus events
     */
    public void unregister() {
        try {
            Dhis2Application.bus.unregister(this);
        } catch (Exception e) {
        }
    }

    /**
     * Singleton constructor
     *
     * @return
     */
    public static PullController getInstance() {
        if (instance == null) {
            instance = new PullController();
        }
        return instance;
    }

    /**
     * Launches the pull process:
     * - Loads metadata from dhis2 server
     * - Wipes app database
     * - Turns SDK into APP data
     *
     * @param ctx
     */
    public void pull(Context ctx) {
        Log.d(TAG, "Starting PULL process...");
        context = ctx;
        try {

            //Register for event bus
            register();
            //Enabling resources to pull
            enableMetaDataFlags();
            //Delete previous metadata
            TrackerController.setMaxEvents(MAX_EVENTS_X_ORGUNIT_PROGRAM);
            MetaDataController.clearMetaDataLoadedFlags();
            MetaDataController.wipe();
            //Fixme delete the events
            Log.d(TAG,"Delete sdk db");
            PopulateDB.wipeSDKData();

            //Pull new metadata
            postProgress(context.getString(R.string.progress_pull_downloading));
            try {
                job = DhisService.loadData(context);
            } catch (Exception ex) {
                Log.e(TAG, "pullS: " + ex.getLocalizedMessage());
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            Log.e(TAG, "pull: " + ex.getLocalizedMessage());
            unregister();
            postException(ex);
        }
    }

    /**
     * Enables loading all metadata
     */
    private void enableMetaDataFlags() {
        LoadingController.enableLoading(context, ResourceType.ASSIGNEDPROGRAMS);
        LoadingController.enableLoading(context, ResourceType.PROGRAMS);
        LoadingController.enableLoading(context, ResourceType.OPTIONSETS);
        LoadingController.enableLoading(context, ResourceType.EVENTS);
    }

    @Subscribe
    public void onLoadMetadataFinished(final NetworkJob.NetworkJobResult<ResourceType> result) {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (result == null) {
                        Log.e(TAG, "onLoadMetadataFinished with null");
                        return;
                    }

                    //Error while pulling
                    if (result.getResponseHolder() != null && result.getResponseHolder().getApiException() != null) {
                        Log.e(TAG, result.getResponseHolder().getApiException().getMessage());
                        postException(new Exception(context.getString(R.string.dialog_pull_error)));
                        return;
                    }

                    //Ok
                    wipeDatabase();
                    convertFromSDK();
                    if (ProgressActivity.PULL_IS_ACTIVE) {
                        Log.d(TAG, "PULL process...OK");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e(TAG, "onLoadMetadataFinished: " + ex.getLocalizedMessage());
                    postException(ex);
                } finally {
                    postFinish();
                    unregister();
                }
            }
        }.start();
    }

    /**
     * Erase data from app database
     */
    public void wipeDatabase() {
        if (!ProgressActivity.PULL_IS_ACTIVE) return;
        Log.d(TAG, "Deleting app database...");
        PopulateDB.wipeDatabase();
    }

    /**
     * Launches visitor that turns SDK data into APP data
     */
    private void convertFromSDK() {
        if (!ProgressActivity.PULL_IS_ACTIVE) return;
        Log.d(TAG, "Converting SDK into APP data");

        //One shared converter to match parents within the hierarchy

        ConvertFromSDKVisitor converter = new ConvertFromSDKVisitor();
        convertMetaData(converter);
        if (!ProgressActivity.PULL_IS_ACTIVE) return;
        convertDataValues(converter);
    }

    /**
     * Turns sdk metadata into app metadata
     *
     * @param converter
     */
    private void convertMetaData(ConvertFromSDKVisitor converter) {
        //OrganisationUnits
        if (!ProgressActivity.PULL_IS_ACTIVE) return;
        postProgress(context.getString(R.string.progress_pull_preparing_orgs));
        Log.i(TAG, "Converting organisationUnits...");
        List<OrganisationUnit> assignedOrganisationsUnits = MetaDataController.getAssignedOrganisationUnits();
        for (OrganisationUnit assignedOrganisationsUnit : assignedOrganisationsUnits) {
            if (!ProgressActivity.PULL_IS_ACTIVE) return;
            OrganisationUnitExtended organisationUnitExtended = new OrganisationUnitExtended(assignedOrganisationsUnit);
            organisationUnitExtended.accept(converter);
        }

    }

    /**
     * Turns events and datavalues into
     *
     * @param converter
     */
    private void convertDataValues(ConvertFromSDKVisitor converter) {
        if (!ProgressActivity.PULL_IS_ACTIVE) return;
        Program appProgram = Program.getFirstProgram();
        String orgUnitName = PreferencesState.getInstance().getOrgUnit();

        postProgress(context.getString(R.string.progress_pull_surveys));
        //XXX This is the right place to apply additional filters to data conversion (only predefined orgunit for instance)
        //For each unit
        for (OrganisationUnit organisationUnit : MetaDataController.getAssignedOrganisationUnits()) {

            //Only events for the right ORGUNIT are loaded
            if(organisationUnit.getLabel() == null || !organisationUnit.getLabel().equals(orgUnitName)){
                continue;
            }

            //Each assigned program
            for (org.hisp.dhis.android.sdk.persistence.models.Program program : MetaDataController.getProgramsForOrganisationUnit(organisationUnit.getId(), ProgramType.WITHOUT_REGISTRATION)) {

                //Only events for the right PROGRAM are loaded
                if(!appProgram.getUid().equals(program.getUid())){
                    continue;
                }

                List<Event> events = TrackerController.getEvents(organisationUnit.getId(), program.getUid());
                Log.i(TAG, String.format("Converting surveys and values for orgUnit: %s | program: %s", organisationUnit.getLabel(), program.getDisplayName()));
                // Visit all the events and save them in block
                int i=0;
                for (Event event : events) {
                    postProgress(context.getString(R.string.progress_pull_building_survey) + String.format(" %s/%s",i++, events.size()));
                    if (!ProgressActivity.PULL_IS_ACTIVE) return;
                    EventExtended eventExtended = new EventExtended(event);

                    //Only last X months
                    if(eventExtended.isTooOld()) continue;
                    eventExtended.accept(converter);
                }
                new SaveModelTransaction<>(ProcessModelInfo.withModels(converter.getSurveys())).onExecute();

                // Visit all the Values and save them in block
                i=0;
                for (Event event : events) {
                    //Visit its values
                    for(DataValue dataValue:event.getDataValues()){
                        if(++i%50==0)
                            postProgress(context.getString(R.string.progress_pull_building_value) + String.format(" %s",i));
                        DataValueExtended dataValueExtended=new DataValueExtended(dataValue);
                        dataValueExtended.accept(converter);
                    }
                }
                new SaveModelTransaction<>(ProcessModelInfo.withModels(converter.getValues())).onExecute();
            }
        }

    }

    /**
     * Notifies a progress into the bus (the caller activity will be listening)
     *
     * @param msg
     */
    private void postProgress(String msg) {
        Dhis2Application.getEventBus().post(new SyncProgressStatus(msg));
    }

    /**
     * Notifies an exception while pulling
     *
     * @param ex
     */
    private void postException(Exception ex) {
        Dhis2Application.getEventBus().post(new SyncProgressStatus(ex));
    }

    /**
     * Notifies that the pull is over
     */
    private void postFinish() {
        //Fixme maybe it is not the best place to reload the logged user.(Without reload the user after pull, the user had diferent id and application crash).
        User user = User.getLoggedUser();
        Session.setUser(user);
        Dhis2Application.getEventBus().post(new SyncProgressStatus());
    }

    //Returns true if the pull thead is finish
    public boolean finishPullJob() {
        if (JobExecutor.isJobRunning(job.getJobId())) {
            Log.d(TAG, "Job " + job.getJobId() + " is running");
            job.cancel(true);
            try {
                try {
                    JobExecutor.getInstance().dequeueRunningJob(job);} catch (Exception e) {e.printStackTrace();}
                job.cancel(true);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                return true;
            }
        }
        return false;

    }

}
