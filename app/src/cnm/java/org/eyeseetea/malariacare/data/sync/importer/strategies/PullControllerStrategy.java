package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.datasources.DeviceDataSource;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.repositories.IDeviceRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ReadPolicy;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.network.ServerAPIController;

public class PullControllerStrategy extends APullControllerStrategy {
    public PullControllerStrategy(PullController pullController) {
        super(pullController);
    }

    @Override
    public void pull(PullFilters pullFilters, IPullController.Callback callback, Context context) {
        Log.d(TAG, "Starting PULL process...");
        try {

            callback.onStep(PullStep.METADATA);
            mPullController.populateMetadataFromCsvs(pullFilters.isAutoConfig());
            OrganisationUnit organisationUnit = null;
            if(pullFilters.isAutoConfig()) {
                organisationUnit = getOrgUnitByPhone(context, callback);

            }
            if (organisationUnit != null|| pullFilters.isDemo()) {
                callback.onComplete();
            } else {
                mPullController.pullMetada(pullFilters, callback);
            }
        } catch (Exception ex) {
            Log.e(TAG, "pull: " + ex.getLocalizedMessage());
            ex.printStackTrace();
            callback.onError(ex);
        }
    }

    private OrganisationUnit getOrgUnitByPhone(Context context,
            final IPullController.Callback callback)
            throws NetworkException, ApiCallException {

        IOrganisationUnitRepository organisationUnitRepository = new OrganisationUnitRepository();
        IDeviceRepository deviceRepository = new DeviceDataSource();
        AuthenticationManager authenticationManager = new AuthenticationManager(context);

        OrganisationUnit organisationUnit = organisationUnitRepository.getCurrentOrganisationUnit(
                ReadPolicy.CACHE);
        if (organisationUnit == null) {
            organisationUnit = organisationUnitRepository.getOrganisationUnitByPhone(
                    deviceRepository.getDevice());
            organisationUnitRepository.saveCurrentOrganisationUnit(organisationUnit);
            if (organisationUnit != null) {
                authenticationManager.login(Credentials.createAutoconfiguredCredentials(
                        ServerAPIController.getServerUrl()),
                        new IAuthenticationManager.Callback<UserAccount>() {
                            @Override
                            public void onSuccess(UserAccount result) {
                                Log.d(TAG, "Create autoconfigured user");
                            }

                            @Override
                            public void onError(Throwable throwable) {
                                Log.e(TAG, "Create autoconfigured user error");
                                callback.onError(throwable);
                            }
                        });
            }
        }
        return organisationUnit;
    }

    @Override
    public void convertMetadata(ConvertFromSDKVisitor converter) {

    }
}
