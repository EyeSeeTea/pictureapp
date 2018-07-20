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
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.utils.PopulateDBStrategy;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.data.remote.PullDhisSDKDataSource;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.sync.importer.models.OrganisationUnitExtended;
import org.eyeseetea.malariacare.data.sync.importer.strategies.APullControllerStrategy;
import org.eyeseetea.malariacare.data.sync.importer.strategies.PullControllerStrategy;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.eyeseetea.malariacare.domain.exception.WarningException;
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
    private APullControllerStrategy mPullControllerStrategy;
    private Context mContext;
    private boolean cancelPull;


    public PullController(Context context) {
        mContext = context;

        mPullRemoteDataSource = new PullDhisSDKDataSource();
        mConverter = new ConvertFromSDKVisitor(context);
        mDataConverter = new DataConverter(context);
        mPullControllerStrategy = new PullControllerStrategy(this, mContext);
    }

    @Override
    public void pull(final PullFilters pullFilters, final Callback callback) {
        mPullControllerStrategy.pull(pullFilters, callback, mContext);
    }

    @Override
    public void cancel() {
        cancelPull = true;
    }

    public void pullMetadata(final PullFilters pullFilters, final Callback callback) {
        if (cancelPull) {
            callback.onCancel();
            return;
        }
        if (pullFilters.pullMetaData()) {
            mPullRemoteDataSource.pullMetadata(
                    new IDataSourceCallback<List<OrganisationUnit>>() {
                        @Override
                        public void onSuccess(final List<OrganisationUnit> organisationUnits) {
                                if(pullFilters.getDataByOrgUnit()!=null && !pullFilters.getDataByOrgUnit().equals("")) {
                                    if (!pullFilters.downloadData()
                                            || pullFilters.pullDataAfterMetadata()) {
                                        pullAndConvertOuOptions(pullFilters.getDataByOrgUnit(),callback);
                                        convertMetaData(callback);
                                    } else {
                                        pullAndConvertOuOptions(pullFilters.getDataByOrgUnit(),callback);
                                        convertMetaData(new Callback() {
                                            @Override
                                            public void onComplete() {
                                                pullData(pullFilters, organisationUnits, callback);
                                            }

                                            @Override
                                            public void onCancel() {
                                                callback.onCancel();
                                            }

                                            @Override
                                            public void onStep(PullStep step) {
                                                callback.onStep(step);
                                            }

                                            @Override
                                            public void onError(Throwable throwable) {
                                                callback.onError(throwable);
                                            }

                                            @Override
                                            public void onWarning(WarningException warning) {
                                                callback.onWarning(warning);
                                            }
                                        });

                                    }
                                }else{
                                    convertMetaData(callback);
                                }
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            callback.onError(throwable);
                        }
                    }, pullFilters.getListOfOrgUnitsToPull());
        } else {
            if (pullFilters.downloadData()) {
                List<OrganisationUnit> organisationUnitsList =
                        D2.me().organisationUnits().list().toBlocking().first();
                pullData(pullFilters, organisationUnitsList, callback);
            } else {
                callback.onComplete();
            }
        }
    }

    private void pullAndConvertOuOptions(final String orgUnitName, final Callback callback) {
        OrganisationUnitRepository organisationUnitRepository = new OrganisationUnitRepository();
        String orgUnituId = organisationUnitRepository.getOrganisationUnitByName(orgUnitName).getUid();
        mPullControllerStrategy.pullAndCovertOuOptions(orgUnituId, callback);
    }

    public void populateMetadataFromCsvs(boolean isDemo) throws IOException {
        PopulateDB.initDataIfRequired(mContext);
        if (isDemo) {
            new PopulateDBStrategy().createDummyOrgUnitsDataInDB(mContext);
            new PopulateDBStrategy().createDummyOrganisationInDB();
        }
    }

    public void pullData(final PullFilters pullFilters, List<OrganisationUnit> organisationUnits,
            final Callback callback) {

        if (cancelPull) {
            callback.onCancel();
            return;
        }

        mPullRemoteDataSource.pullData(pullFilters, organisationUnits,
                new IDataSourceCallback<List<Event>>() {
                    @Override
                    public void onSuccess(List<Event> result) {
                        mPullControllerStrategy.onPullDataComplete(callback, pullFilters.isDemo());
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

            mPullControllerStrategy.convertMetadata(mConverter, callback);
        } catch (NullPointerException ex) {
            callback.onError(new PullConversionException(ex));
        }
    }

    public void convertData(final Callback callback) {

        if (cancelPull) {
            callback.onCancel();
            return;
        }

        callback.onStep(PullStep.CONVERT_DATA);

        try {
            mConverter.setOrgUnitDBs(OrgUnitDB.getAllOrgUnit());
            mDataConverter.convert(callback, mConverter);
        } catch (NullPointerException ex) {
            callback.onError(new PullConversionException(ex));
        }
    }

    public void onPullDataComplete(Callback callback, boolean isDemo) {
        mPullControllerStrategy.onPullDataComplete(callback, isDemo);
    }
}
