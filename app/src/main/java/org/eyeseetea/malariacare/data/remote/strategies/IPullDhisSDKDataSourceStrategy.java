package org.eyeseetea.malariacare.data.remote.strategies;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.hisp.dhis.client.sdk.core.event.EventFilters;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.List;

public interface IPullDhisSDKDataSourceStrategy {
    void setEventFilters(EventFilters eventFilters);
    void pullMetadata(IPullDhisSDKDataSourceStrategy mPullDhisSDKDataSourceStrategy,  final IDataSourceCallback<List<OrganisationUnit>> callback);
    void onMetadataSucceed(IDataSourceCallback callback, List<OrganisationUnit> organisationUnits);
}
