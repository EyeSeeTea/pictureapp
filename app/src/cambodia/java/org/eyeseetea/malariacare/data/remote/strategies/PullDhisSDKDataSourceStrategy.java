package org.eyeseetea.malariacare.data.remote.strategies;

import org.hisp.dhis.client.sdk.core.event.EventFilters;

public class PullDhisSDKDataSourceStrategy implements IPullDhisSDKDataSourceStrategy {
    public void setEventFilters(EventFilters eventFilters) {

    }

    @Override
    public void onMetadataSucceed(IDataSourceCallback callback,
            List<OrganisationUnit> organisationUnits) {
        callback.onSuccess(organisationUnits);
    }
}
