package org.eyeseetea.malariacare.data.remote.strategies;

import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.hisp.dhis.client.sdk.core.event.EventFilters;

public class PullDhisSDKDataSourceStrategy implements IPullDhisSDKDataSourceStrategy {
    String mCategoryOptionUID = null;
    private static final String CATEGORY_COMBINATION_UID = "GwFkNOXaQcq";

    public void setEventFilters(EventFilters eventFilters) {
        String categoryOptionUID = getCategoryOptionUIDByCurrentUser();

        if (categoryOptionUID != null) {
            eventFilters.setCategoryCombinationAttribute(CATEGORY_COMBINATION_UID);
            eventFilters.setCategoryOptionAttribute(categoryOptionUID);
        }
    }

    private String getCategoryOptionUIDByCurrentUser() {
        if (mCategoryOptionUID == null) {
            mCategoryOptionUID = SdkQueries.getCategoryOptionUIDByCurrentUser();
        }

        return mCategoryOptionUID;
    }
}
