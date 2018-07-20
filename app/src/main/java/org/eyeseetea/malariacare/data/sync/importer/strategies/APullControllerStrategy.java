package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.data.sync.importer.models.OrganisationUnitExtended;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;

import java.util.List;

public abstract class APullControllerStrategy {

    protected static String TAG = "PullController";
    protected PullController mPullController;

    public APullControllerStrategy(
            PullController pullController) {
        mPullController = pullController;
    }

    public abstract void convertMetadata(ConvertFromSDKVisitor converter,
            IPullController.Callback callback);

    public void pull(final PullFilters pullFilters, final IPullController.Callback callback,
            Context context) {
        Log.d(TAG, "Starting PULL process...");
        try {

            callback.onStep(PullStep.METADATA);

            mPullController.populateMetadataFromCsvs(pullFilters.isDemo());

            if (pullFilters.isDemo()) {
                callback.onComplete();
            } else {

                mPullController.pullMetadata(pullFilters, callback);
            }
        } catch (Exception ex) {
            Log.e(TAG, "pull: " + ex.getLocalizedMessage());
            callback.onError(ex);
        }
    }

    public void onPullDataComplete(final IPullController.Callback callback, boolean isDemo) {
        mPullController.convertData(callback);
        callback.onComplete();
    }

    public void convertOrgUnitOptions(List<OrganisationUnitExtended> organisationUnitExtendeds) {
    }

    public void pullAndCovertOuOptions(final String selectedUid,
            final IPullController.Callback callback) {
        AOrgUnitToOptionConverterStrategy orgUnitToOptionConverterStrategy = new OrgUnitToOptionConverterStrategy();
        orgUnitToOptionConverterStrategy.convert();
    }
}
