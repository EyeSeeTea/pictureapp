package org.eyeseetea.malariacare.data.remote.strategies;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.hisp.dhis.client.sdk.core.event.EventFilters;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.List;

public class PullDhisSDKDataSourceStrategy implements IPullDhisSDKDataSourceStrategy {
    public void setEventFilters(EventFilters eventFilters) {

    }

    @Override
    public void onMetadataSucceed(IDataSourceCallback callback,
            List<OrganisationUnit> organisationUnits) {
        callback.onSuccess(organisationUnits);
    }
}
