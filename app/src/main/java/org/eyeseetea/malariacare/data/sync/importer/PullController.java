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

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.data.remote.PullDhisSDKDataSource;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
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
    private boolean cancelPull;

    public PullController(Context context) {
        mContext = context;

        mPullRemoteDataSource = new PullDhisSDKDataSource();
        mConverter = new ConvertFromSDKVisitor();
    }

    @Override
    public void pull(final PullFilters pullFilters, final Callback callback) {
        Log.d(TAG, "Starting PULL process...");
        try {

            callback.onStep(PullStep.METADATA);

            populateMetadataFromCsvs(pullFilters.isDemo());

            if (pullFilters.isDemo() || !pullFilters.downloadData()) {
                callback.onComplete();
            } else {

                pullMetada(pullFilters, callback);
            }
        } catch (Exception ex) {
            Log.e(TAG, "pull: " + ex.getLocalizedMessage());
            callback.onError(ex);
        }
    }

    @Override
    public void cancel() {
        cancelPull = true;
    }

    private void pullMetada(final PullFilters pullFilters, final Callback callback) {
        if (cancelPull) {
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

    private void populateMetadataFromCsvs(boolean isDemo) throws IOException {
        PopulateDB.initDataIfRequired(mContext);

        if (isDemo) {
            createDummyOrgUnitsDataInDB();
        }
    }

    private void createDummyOrgUnitsDataInDB() {
        List<OrgUnit> orgUnits = OrgUnit.getAllOrgUnit();

        if (orgUnits.size() == 0) {
            try {
                PopulateDB.populateDummyData(mContext);
                OrgUnitToOptionConverter.convert();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void pullData(PullFilters pullFilters, List<OrganisationUnit> organisationUnits,
            final Callback callback) {

        if (cancelPull) {
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

        if (cancelPull) {
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

        OrgUnitToOptionConverter.convert();
    }

    private void convertData(final Callback callback) {

        if (cancelPull) {
            callback.onCancel();
            return;
        }

        callback.onStep(PullStep.CONVERT_DATA);

        DataConverter.convert(callback, mConverter);
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
