package org.eyeseetea.malariacare.data.remote.strategies;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.hisp.dhis.client.sdk.core.event.EventFilters;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.List;
import java.util.Set;

public interface IPullDhisSDKDataSourceStrategy {
    void setEventFilters(EventFilters eventFilters);
    void pullMetadata(IPullDhisSDKDataSourceStrategy mPullDhisSDKDataSourceStrategy,  final IDataSourceCallback<List<OrganisationUnit>> callback);
    void pullMetadata(IPullDhisSDKDataSourceStrategy mPullDhisSDKDataSourceStrategy,  final IDataSourceCallback<List<OrganisationUnit>> callback, Set<String> uids);
    void onMetadataSucceed(IDataSourceCallback callback, List<OrganisationUnit> organisationUnits);
}
