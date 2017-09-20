package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.util.Log;

import org.eyeseetea.malariacare.data.database.datasources.DeviceDataSource;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.repositories.IDeviceRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;

public class PullControllerStrategy extends APullControllerStrategy {
    public PullControllerStrategy(PullController pullController) {
        super(pullController);
    }

    @Override
    public void pull(PullFilters pullFilters, IPullController.Callback callback) {
        Log.d(TAG, "Starting PULL process...");
        try {

            callback.onStep(PullStep.METADATA);
            mPullController.populateMetadataFromCsvs(pullFilters.isDemo());
            OrganisationUnit organisationUnit = null;
            if(pullFilters.isAutoConfig()) {
                organisationUnit = getOrgUnitByPhone();
            }
            if (organisationUnit != null|| pullFilters.isDemo()) {
                callback.onComplete();
            } else {
                mPullController.pullMetada(pullFilters, callback);
            }
        } catch (Exception ex) {
            Log.e(TAG, "pull: " + ex.getLocalizedMessage());
            callback.onError(ex);
        }
    }

    private OrganisationUnit getOrgUnitByPhone() throws NetworkException, ApiCallException {

        IOrganisationUnitRepository organisationUnitRepository = new OrganisationUnitRepository();
        IDeviceRepository deviceRepository = new DeviceDataSource();

        return organisationUnitRepository.getOrganisationUnitByPhone(deviceRepository.getDevice());
    }

    @Override
    public void convertMetadata(ConvertFromSDKVisitor converter) {

    }
}
