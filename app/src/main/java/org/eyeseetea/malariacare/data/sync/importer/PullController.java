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

import static org.eyeseetea.malariacare.domain.usecase.pull.PullStep.BUILDING_SURVEYS;
import static org.eyeseetea.malariacare.domain.usecase.pull.PullStep.BUILDING_VALUES;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.QuestionOption;
import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.model.Value;
import org.eyeseetea.malariacare.data.database.utils.PopulateDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.remote.PullDhisSDKDataSource;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.sync.importer.models.DataValueExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;
import org.eyeseetea.malariacare.data.sync.importer.models.OrganisationUnitExtended;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.io.IOException;
import java.util.List;

public class PullController implements IPullController {
    private static String TAG = "PullController";

    PullDhisSDKDataSource mPullRemoteDataSource = new PullDhisSDKDataSource();
    ConvertFromSDKVisitor mConverter = new ConvertFromSDKVisitor();
    private Context mContext;
    private boolean cancellPull;

    public PullController(Context context) {
        mContext = context;

        mPullRemoteDataSource = new PullDhisSDKDataSource();
        mConverter = new ConvertFromSDKVisitor();
    }

    public void pull(final PullFilters pullFilters, final Callback callback) {
        Log.d(TAG, "Starting PULL process...");
        try {

            callback.onStep(PullStep.METADATA);

            populateMetadataFromCsvs(pullFilters.isDemo());

            if (pullFilters.isDemo()) {
                callback.onComplete();
            } else {

                if (cancellPull) {
                    callback.onCancel();
                    return;
                }

                mPullRemoteDataSource.pullMetadata(
                        new IDataSourceCallback<List<OrganisationUnit>>() {
                            @Override
                            public void onSuccess(List<OrganisationUnit> organisationUnits) {
                                pullData(pullFilters, organisationUnits, callback);
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                callback.onError(throwable);
                            }
                        });
            }
        } catch (Exception ex) {
            Log.e(TAG, "pull: " + ex.getLocalizedMessage());
            callback.onError(ex);
        }
    }

    @Override
    public void cancel() {
        cancellPull = true;
    }

    private void populateMetadataFromCsvs(boolean isDemo) throws IOException {
        PopulateDB.initDataIfRequired(mContext.getAssets());

        if (isDemo) {
            createDummyOrgUnitsDataInDB();
        }
    }

    private void createDummyOrgUnitsDataInDB() {
        List<OrgUnit> orgUnits = OrgUnit.getAllOrgUnit();

        if (orgUnits.size() == 0) {
            try {
                PopulateDB.populateDummyData(mContext.getAssets());
                convertOUinOptions();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void convertOUinOptions() {
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

    private void pullData(PullFilters pullFilters, List<OrganisationUnit> organisationUnits,
            final Callback callback) {

        if (cancellPull) {
            callback.onCancel();
            return;
        }

        mPullRemoteDataSource.pullData(pullFilters, organisationUnits,
                new IDataSourceCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> result) {
                PopulateDB.wipeDatabase();

                convertFromSDK(callback);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });
    }


    private void convertFromSDK(final Callback callback) {
        Log.d(TAG, "Converting SDK into APP data");

        try {
            convertMetaData(callback);
            convertData(callback);
        } catch (Exception ex) {
            callback.onError(new PullConversionException());
        }


    }

    private void convertMetaData(final Callback callback) {

        if (cancellPull) {
            callback.onCancel();
            return;
        }

        callback.onStep(PullStep.CONVERT_METADATA);
        Log.d(TAG, "Converting organisationUnits...");

        List<OrganisationUnitExtended> assignedOrganisationsUnits =
                OrganisationUnitExtended.getExtendedList(
                        (SdkQueries.getAssignedOrganisationUnits()));

        for (OrganisationUnitExtended assignedOrganisationsUnit : assignedOrganisationsUnits) {
            assignedOrganisationsUnit.accept(mConverter);
        }

        convertOUinOptions();
    }

    private void convertData(final Callback callback) {

        if (cancellPull) {
            callback.onCancel();
            return;
        }

        callback.onStep(PullStep.CONVERT_DATA);

        String orgUnitName = PreferencesState.getInstance().getOrgUnit();

        List<OrgUnit> orgUnits = mConverter.getOrgUnits();

        for (OrgUnit orgUnit : orgUnits) {

            //Only events for the right ORGUNIT are loaded
            if (!orgUnitName.isEmpty() &&
                    orgUnit.getName() != null && !orgUnit.getName().equals(
                    orgUnitName)) {
                continue;
            }

            List<Program> programs = Program.getAllPrograms();

            for (Program program : programs) {
                List<EventExtended> events = EventExtended.getExtendedList(

                        SdkQueries.getEvents(orgUnit.getUid(), program.getUid()));
                Log.d(TAG,
                        String.format("Converting surveys and values for orgUnit: %s | program: %s",
                                orgUnit.getChildren(), program.getName()));

                callback.onStep(BUILDING_SURVEYS);

                int i = 0;
                for (EventExtended event : events) {
                    Log.d(TAG, mContext.getString(R.string.progress_pull_building_survey)
                            + String.format(" %s/%s", i++, events.size()));

                    event.accept(mConverter);
                }

                callback.onStep(BUILDING_VALUES);

                i = 0;
                for (EventExtended event : events) {

                    List<DataValueExtended> dataValues = DataValueExtended.getExtendedList(
                            SdkQueries.getDataValues(event.getUid()));

                    for (DataValueExtended dataValueExtended : dataValues) {
                        if (++i % 50 == 0) {
                            Log.d(TAG, mContext.getString(R.string.progress_pull_building_value)
                                    + String.format(" %s", i));
                        }
                        dataValueExtended.accept(mConverter);
                    }
                }
            }

        }

        saveConvertedSurveys(callback);

    }

    private void saveConvertedSurveys(final Callback callback) {

        if (cancellPull) {
            callback.onCancel();
            return;
        }

        List<Survey> surveys = mConverter.getSurveys();

        Survey.saveAll(surveys, new IDataSourceCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                saveConvertedValues(callback);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(new PullConversionException(throwable));
            }
        });
    }

    private void saveConvertedValues(final Callback callback) {

        if (cancellPull) {
            callback.onCancel();
            return;
        }

        List<Value> values = mConverter.getValues();

        Value.saveAll(values, new IDataSourceCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                callback.onComplete();
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(new PullConversionException(throwable));
            }
        });
    }

    //TODO jsanchez
    //public static final int MAX_EVENTS_X_ORGUNIT_PROGRAM = 4800;

    //TODO jsanchez
    /**
     * Returns the correct data from the limited date in shared preferences
     */
/*    private Date getDateFromString(String selectedDateLimit) {
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
    }*/

    //TODO jsanchez
/*
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
}
