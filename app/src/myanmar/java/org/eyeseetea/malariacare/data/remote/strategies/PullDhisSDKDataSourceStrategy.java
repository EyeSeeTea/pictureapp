package org.eyeseetea.malariacare.data.remote.strategies;

import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.core.event.EventFilters;
import org.hisp.dhis.client.sdk.models.category.CategoryOption;

import java.util.List;

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
            String userName =
                    D2.me().userCredentials().toBlocking().single().getUsername().toLowerCase();
            List<CategoryOption> categoryOptions =
                    D2.categoryOptions().list().toBlocking().single();

            for (CategoryOption categoryOption : categoryOptions) {
                if (categoryOption.getCode() != null
                        && categoryOption.getCode().toLowerCase().equals(
                        userName)) {
                    mCategoryOptionUID = categoryOption.getUId();
                }
            }
        }

        return mCategoryOptionUID;
    }
}
