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
import org.eyeseetea.malariacare.data.database.utils.PopulateDBStrategy;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.data.remote.PullDhisSDKDataSource;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.sync.importer.models.OrganisationUnitExtended;
import org.eyeseetea.malariacare.data.sync.importer.strategies.APullControllerStrategy;
import org.eyeseetea.malariacare.data.sync.importer.strategies.PullControllerStrategy;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.models.event.Event;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.io.IOException;
import java.util.List;

public class PullController implements IPullController {
    private static String TAG = "PullController";

    PullDhisSDKDataSource mPullRemoteDataSource = new PullDhisSDKDataSource();
    ConvertFromSDKVisitor mConverter;
    DataConverter mDataConverter;
    private APullControllerStrategy mPullControllerStrategy = new PullControllerStrategy();
    private Context mContext;
    private boolean cancelPull;


    public PullController(Context context) {
        mContext = context;

        mPullRemoteDataSource = new PullDhisSDKDataSource();
        mConverter = new ConvertFromSDKVisitor(context);
        mDataConverter = new DataConverter(context);
    }

    @Override
    public void pull(final PullFilters pullFilters, final Callback callback) {
        Log.d(TAG, "Starting PULL process...");

        try {

            callback.onStep(PullStep.METADATA);

            populateMetadataFromCsvs(pullFilters.isDemo());

            if (pullFilters.isDemo()) {
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
        if(pullFilters.pullMetaData()) {
            mPullRemoteDataSource.pullMetadata(
                    new IDataSourceCallback<List<OrganisationUnit>>() {
                        @Override
                        public void onSuccess(List<OrganisationUnit> organisationUnits) {
                            if (!pullFilters.downloadData() || pullFilters.pullDataAfterMetadata()) {
                                convertMetaData(callback);
                                callback.onComplete();
                            } else {
                                convertMetaData(callback);
                                pullData(pullFilters, organisationUnits, callback);
                            }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            callback.onError(throwable);
                        }
                    });
        }
        else {
            if (pullFilters.downloadData()) {
                List<OrganisationUnit> organisationUnitsList = D2.me().organisationUnits().list().toBlocking().first();
                pullData(pullFilters, organisationUnitsList, callback);
            }
            else{
                callback.onComplete();
            }
        }
    }

    private void populateMetadataFromCsvs(boolean isDemo) throws IOException {
        PopulateDB.initDataIfRequired(mContext);

        if (isDemo) {
            new PopulateDBStrategy().createDummyOrgUnitsDataInDB(mContext);
            new PopulateDBStrategy().createDummyOrganisationInDB();
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
                        convertData(callback);
                        callback.onComplete();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        callback.onError(throwable);
                    }
                });
    }

    private void convertMetaData(final Callback callback) {

        if (cancelPull) {
            callback.onCancel();
            return;
        }

        callback.onStep(PullStep.CONVERT_METADATA);
        Log.d(TAG, "Converting organisationUnits...");
        try {
            List<OrganisationUnitExtended> assignedOrganisationsUnits =
                    OrganisationUnitExtended.getExtendedList(
                            (SdkQueries.getAssignedOrganisationUnits()));
            for (OrganisationUnitExtended assignedOrganisationsUnit : assignedOrganisationsUnits) {
                assignedOrganisationsUnit.accept(mConverter);
            }

            OrgUnitToOptionConverter.convert();
            mPullControllerStrategy.convertMetadata(mConverter);
        } catch (Exception ex) {
            ex.printStackTrace();
            callback.onError(new PullConversionException());
        }
    }

    private void convertData(final Callback callback) {

        if (cancelPull) {
            callback.onCancel();
            return;
        }

        callback.onStep(PullStep.CONVERT_DATA);

        try {
            mConverter.setOrgUnits(OrgUnit.getAllOrgUnit());
            mDataConverter.convert(callback, mConverter);
        } catch (Exception ex) {
            ex.printStackTrace();
            callback.onError(new PullConversionException());
        }
    }
}
