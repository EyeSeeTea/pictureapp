package org.eyeseetea.malariacare.data.remote.strategies;

import org.hisp.dhis.client.sdk.core.event.EventFilters;

public interface IPullDhisSDKDataSourceStrategy {
    void setEventFilters(EventFilters eventFilters);
}
