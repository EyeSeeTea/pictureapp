package org.eyeseetea.malariacare.data.remote.strategies;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.hisp.dhis.client.sdk.core.event.EventFilters;

public class PullDhisSDKDataSourceStrategy implements IPullDhisSDKDataSourceStrategy {
    String mCategoryOptionUID = null;

    public void setEventFilters(EventFilters eventFilters) {
        String categoryOptionUID = getCategoryOptionUIDByCurrentUser();

        if (categoryOptionUID != null) {
            eventFilters.setCategoryCombinationAttribute(
                    PreferencesState.getInstance().getContext().getString(
                            R.string.category_combination));
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
