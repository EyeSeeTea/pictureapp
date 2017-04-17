package org.eyeseetea.malariacare.data.sync.exporter.strategies;

import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.sync.importer.models.EventExtended;

public class ConvertToSdkVisitorStrategy {


    private static String mCategoryOptionUID;

    public static String getAttributeCategoryOptions() {
        return getCategoryOptionUIDByCurrentUser();
    }

    private static String getCategoryOptionUIDByCurrentUser() {
        if (mCategoryOptionUID == null) {
            mCategoryOptionUID = SdkQueries.getCategoryOptionUIDByCurrentUser();
        }

        return mCategoryOptionUID;
    }

    public static void setAttributeCategoryOptionsInEvent(EventExtended event) {
        event.getEvent().setAttributeCategoryOptions(
                ConvertToSdkVisitorStrategy.getAttributeCategoryOptions());
    }
}
