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

package org.eyeseetea.malariacare.data.sync.importer;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.ProgressActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.QuestionOption;
import org.eyeseetea.malariacare.data.database.utils.PopulateDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.remote.PullDhisSDKDataSource;
import org.eyeseetea.malariacare.data.remote.SdkController;
import org.eyeseetea.malariacare.data.remote.SdkPullController;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.sync.importer.models.DataValueExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.OrganisationUnitExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.ProgramExtended;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.hisp.dhis.client.sdk.models.program.ProgramType;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PullController implements IPullController {
    private static String TAG = "PullController";
    //public static final int MAX_EVENTS_X_ORGUNIT_PROGRAM = 4800;

    PullDhisSDKDataSource mPullDhisSDKDataSource = new PullDhisSDKDataSource();
    private Context mContext;

    public PullController(Context context) {
        mContext = context;
    }

    public void pull(final Callback callback) {
        Log.d(TAG, "Starting PULL process...");
        try {

            callback.onStep(PullStep.METADATA);

            populateMetadataFromCsvs();


            mPullDhisSDKDataSource.pullMetadata(new IDataSourceCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    callback.onComplete();
                }

                @Override
                public void onError(Throwable throwable) {
                    callback.onError(throwable);
                }
            });

            //TODO jsanchez
/*            //clear flags
            SdkPullController.clearPullFlags(PreferencesState.getInstance().getContext());
            //Enabling resources to pull
            SdkPullController.enableMetaDataFlags(PreferencesState.getInstance().getContext());
            //Delete previous metadata

            Log.d(TAG, "Delete sdk db");
            PopulateDB.wipeSDKData();
            //Pull new metadata
            postProgress(mContext.getString(R.string.progress_pull_downloading));
            PreferencesState.getInstance().reloadPreferences();

            SdkPullController.clearMetaDataLoadedFlags();
            SdkPullController.wipe();

            SdkPullController.setMaxEvents(MAX_EVENTS_X_ORGUNIT_PROGRAM);
            String selectedDateLimit = PreferencesState.getInstance().getDataLimitedByDate();

            //Limit of data by date is selected
            if (BuildConfig.loginDataDownloadPeriod) {
                SdkPullController.setStartDate(
                        EventExtended.format(getDateFromString(selectedDateLimit),
                                EventExtended.AMERICAN_DATE_FORMAT));
            }

            if (selectedDateLimit.equals(
                    PreferencesState.getInstance().getContext().getString(R.string.no_data))) {
                pullMetaData();
            } else {
                pullMetaDataAndData();
            }*/
        } catch (Exception ex) {
            Log.e(TAG, "pull: " + ex.getLocalizedMessage());
            callback.onError(ex);
        }
    }

    private void populateMetadataFromCsvs() throws IOException {
        PopulateDB.initDataIfRequired(mContext.getAssets());
    }

    public static void convertOUinOptions() {
        List<Question> questions = Question.getAllQuestionsWithOrgUnitDropdownList();
        //remove older values, but not the especial "other" option
        for (Question question : questions) {
            List<Option> options = question.getAnswer().getOptions();
            removeOldValues(question, options);
        }

        if (questions.size() == 0) {
            return;
        }

        //Generate the orgUnits options for each question with orgunit dropdown list
        List<OrgUnit> orgUnits = OrgUnit.getAllOrgUnit();
        for (OrgUnit orgUnit : orgUnits) {
            addOUOptionToQuestions(questions, orgUnit);
        }
    }

    public static void addOUOptionToQuestions(List<Question> questions, OrgUnit orgUnit) {
        for (Question question : questions) {
            Option option = new Option();
            option.setAnswer(question.getAnswer());
            option.setName(orgUnit.getUid());
            option.setCode(orgUnit.getName());
            option.save();
        }
    }

    public static void removeOldValues(Question question, List<Option> options) {
        for (Option option : options) {
            if (QuestionOption.findByQuestionAndOption(question, option).size() == 0) {
                option.delete();
            }
        }
    }

    private void pullMetaData() {
        try {
            SdkPullController.loadMetaData();
        } catch (Exception ex) {
            Log.e(TAG, "pullS: " + ex.getLocalizedMessage());
            postException(ex);
        }
    }

    private void pullMetaDataAndData() {
        try {
            SdkPullController.loadMetaDataAndData();
        } catch (Exception ex) {
            Log.e(TAG, "pullS: " + ex.getLocalizedMessage());
            ex.printStackTrace();
            return;
        }
    }

    /**
     * Returns the correct data from the limited date in shared preferences
     */
    private Date getDateFromString(String selectedDateLimit) {
        Calendar day = Calendar.getInstance();
        if (selectedDateLimit.equals(
                PreferencesState.getInstance().getContext().getString(R.string.last_6_days))) {
            day.add(Calendar.DAY_OF_YEAR, -6);
        } else if (selectedDateLimit.equals(
                PreferencesState.getInstance().getContext().getString(R.string.last_6_weeks))) {
            day.add(Calendar.WEEK_OF_YEAR, -6);
        } else if (selectedDateLimit.equals(
                PreferencesState.getInstance().getContext().getString(R.string.last_6_months))) {
            day.add(Calendar.MONTH, -6);
        }
        return day.getTime();
    }

    /**
     * @Subscribe public void onLoadMetadataFinished(final NetworkJob.NetworkJobResult<ResourceType>
     * result) {
     * new Thread() {
     * @Override public void run() {
     * try {
     * if (result == null) {
     * Log.e(TAG, "onLoadMetadataFinished with null");
     * return;
     * }
     *
     * //Error while pulling
     * if (result.getResponseHolder() != null
     * && result.getResponseHolder().getApiException() != null) {
     * postException(new Exception(mContext.getString(R.string.dialog_pull_error)));
     * return;
     * }
     *
     * startConversion();
     * } catch (Exception ex) {
     * onPullException(ex);
     * } finally {
     * onPullFinish();
     * }
     * }
     * }.start();
     * }
     */


    private void onPullException(Exception ex) {
        Log.e(TAG, "onLoadMetadataFinished: " + ex.getLocalizedMessage());
        ex.printStackTrace();
        postException(ex);
    }

    private void onPullFinish() {
        postFinish();
    }

    private void onPullError(String message) {
        postException(new Exception(mContext.getString(R.string.dialog_pull_error)));
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
     */
    private void convertMetaData(ConvertFromSDKVisitor converter) {
        //OrganisationUnits
        if (!ProgressActivity.PULL_IS_ACTIVE) return;
        postProgress(mContext.getString(R.string.progress_pull_preparing_orgs));
        Log.i(TAG, "Converting organisationUnits...");
        List<OrganisationUnitExtended> assignedOrganisationsUnits =
                OrganisationUnitExtended.getExtendedList(
                        (SdkQueries.getAssignedOrganisationUnits()));
        for (OrganisationUnitExtended assignedOrganisationsUnit : assignedOrganisationsUnits) {
            if (!ProgressActivity.PULL_IS_ACTIVE) return;
            assignedOrganisationsUnit.accept(converter);
        }

    }

    /**
     * Turns events and datavalues into
     */
    private void convertDataValues(ConvertFromSDKVisitor converter) {
        if (!ProgressActivity.PULL_IS_ACTIVE) return;
        Program appProgram = Program.getFirstProgram();
        String orgUnitName = PreferencesState.getInstance().getOrgUnit();

        postProgress(mContext.getString(R.string.progress_pull_surveys));
        //XXX This is the right place to apply additional filters to data conversion (only
        // predefined orgunit for instance)
        //For each unit
        for (OrganisationUnitExtended organisationUnit : OrganisationUnitExtended.getExtendedList(
                SdkQueries.getAssignedOrganisationUnits
                        ())) {

            //Only events for the right ORGUNIT are loaded
            if (organisationUnit.getLabel() == null || !organisationUnit.getLabel().equals(
                    orgUnitName)) {
                continue;
            }

            //Each assigned program
            for (ProgramExtended program :
                    ProgramExtended.getExtendedList(SdkQueries.getProgramsForOrganisationUnit(
                            organisationUnit.getId(), ProgramType.WITHOUT_REGISTRATION))) {

                //Only events for the right PROGRAM are loaded
                if (!appProgram.getUid().equals(program.getUid())) {
                    continue;
                }

                List<EventExtended> events = EventExtended.getExtendedList(
                        SdkQueries.getEvents(organisationUnit.getId(),
                                program.getUid()));
                Log.i(TAG,
                        String.format("Converting surveys and values for orgUnit: %s | program: %s",
                                organisationUnit.getLabel(), program.getDisplayName()));
                // Visit all the events and save them in block
                int i = 0;
                for (EventExtended event : events) {
                    postProgress(mContext.getString(R.string.progress_pull_building_survey)
                            + String.format(" %s/%s", i++, events.size()));
                    if (!ProgressActivity.PULL_IS_ACTIVE) return;

                    //Only last X months
                    if (event.isTooOld()) continue;
                    event.accept(converter);
                }
                SdkQueries.saveBatch(converter.getSurveys());

                // Visit all the Values and save them in block
                i = 0;
                for (EventExtended event : events) {
                    //Visit its values
                    for (DataValueExtended dataValueExtended : event.getDataValues()) {
                        if (++i % 50 == 0) {
                            postProgress(mContext.getString(R.string.progress_pull_building_value)
                                    + String.format(" %s", i));
                        }
                        dataValueExtended.accept(converter);
                    }
                }
                SdkQueries.saveBatch(converter.getValues());
            }
        }

    }

    /**
     * Notifies a progress into the bus (the caller activity will be listening)
     */
    private void postProgress(String msg) {
        SdkPullController.postProgress(msg);
    }

    /**
     * Notifies an exception while pulling
     */
    private void postException(Exception ex) {
        SdkPullController.postException(ex);
    }

    /**
     * Notifies that the pull is over
     */
    private void postFinish() {
    }

    //Returns true if the pull thead is finish
    public boolean finishPullJob() {
        return SdkController.finishPullJob();
    }

    public void startConversion() {
        //Ok
        wipeDatabase();
        convertFromSDK();
        //Fixme it should be moved after login
/*        convertOUinOptions();
        if (ProgressActivity.PULL_IS_ACTIVE) {
            Log.d(TAG, "PULL process...OK");
        }*/
    }
}
