package org.eyeseetea.malariacare.data.remote.strategies;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.hisp.dhis.client.sdk.core.event.EventFilters;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.List;

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

    @Override
    public void pullMetadata(final IPullDhisSDKDataSourceStrategy mPullDhisSDKDataSourceStrategy,
            final IDataSourceCallback<List<OrganisationUnit>> callback) {

    }

    @Override
    public void onMetadataSucceed(final IDataSourceCallback callback,
            final List<OrganisationUnit> organisationUnits) {
    }

    private String getCategoryOptionUIDByCurrentUser() {
        if (mCategoryOptionUID == null) {
            mCategoryOptionUID = SdkQueries.getCategoryOptionUIDByCurrentUser();
        }

        return mCategoryOptionUID;
    }
}
