package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.content.Context;
import android.util.Log;

import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.datasources.DeviceDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.sync.importer.CnmApiClient;
import org.eyeseetea.malariacare.data.sync.importer.ConvertFromApiVisitor;
import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.data.sync.importer.models.OrgUnitTree;
import org.eyeseetea.malariacare.domain.AutoconfigureException;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IDeviceRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ReadPolicy;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

import java.util.List;

public class PullControllerStrategy extends APullControllerStrategy {

    IOrganisationUnitRepository organisationUnitRepository;
    IDeviceRepository deviceRepository;
    AuthenticationManager authenticationManager;
    private IAsyncExecutor mAsyncExecutor;
    private IMainExecutor mMainExecutor;

    public PullControllerStrategy(PullController pullController) {
        super(pullController);

        mAsyncExecutor = new AsyncExecutor();
        mMainExecutor = new UIThreadExecutor();
    }

    @Override
    public void pull(PullFilters pullFilters, IPullController.Callback callback, Context context) {
        Log.d(TAG, "Starting PULL process...");
        try {

            callback.onStep(PullStep.METADATA);
            mPullController.populateMetadataFromCsvs(pullFilters.isAutoConfig());
            OrganisationUnit organisationUnit = null;
            if (pullFilters.isAutoConfig()) {
                try {
                    organisationUnit = getOrgUnitByPhone(context, callback);
                } catch (ApiCallException e) {
                    throw new AutoconfigureException();
                }
            }
            if (pullFilters.isDemo()) {
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

        organisationUnitRepository = new OrganisationUnitRepository();
        deviceRepository = new DeviceDataSource();
        authenticationManager = new AuthenticationManager(context);

        OrganisationUnit organisationUnit = organisationUnitRepository.getCurrentOrganisationUnit(
                ReadPolicy.CACHE);
        if (organisationUnit == null) {
            organisationUnit = organisationUnitRepository.getOrganisationUnitByPhone(
                    deviceRepository.getDevice());
            if (organisationUnit != null) {
                organisationUnitRepository.saveCurrentOrganisationUnit(organisationUnit);
            } else {
                callback.onError(new AutoconfigureException());
                return null;
            }

            if (organisationUnit != null) {

                Credentials hardcodedCredentials = getHardcodedCredentials(callback);

                if (hardcodedCredentials != null) {
                    authenticationManager.login(hardcodedCredentials,
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
        }
        return organisationUnit;
    }

    private Credentials getHardcodedCredentials(IPullController.Callback callback) {
        Credentials hardcodedCredentials = null;

        try {
            hardcodedCredentials =
                    authenticationManager.getHardcodedServerCredentials(
                            ServerAPIController.getServerUrl());
        } catch (ConfigJsonIOException e) {
            e.printStackTrace();
            callback.onError(e);
        }

        return hardcodedCredentials;
    }

    @Override
    public void convertMetadata(ConvertFromSDKVisitor converter,
            final IPullController.Callback callback) {
        pullOrganisationUnitTree(new CnmApiClient.CnmApiClientCallBack<List<OrgUnitTree>>() {
            @Override
            public void onSuccess(final List<OrgUnitTree> result) {
                mAsyncExecutor.run(new Runnable() {
                    @Override
                    public void run() {
                        ConvertFromApiVisitor convertFromApiVisitor = new ConvertFromApiVisitor();
                        for (OrgUnitTree orgUnitTree : result) {
                            orgUnitTree.accept(convertFromApiVisitor);
                        }
                        mMainExecutor.run(new Runnable() {
                            @Override
                            public void run() {
                                callback.onComplete();
                            }
                        });
                    }
                });

            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    private void pullOrganisationUnitTree(
            CnmApiClient.CnmApiClientCallBack<List<OrgUnitTree>> cnmApiClientCallBack) {
        CnmApiClient cnmApiClient = null;
        try {
            cnmApiClient = new CnmApiClient(PreferencesState.getInstance().getDhisURL() + "/");
        } catch (Exception e) {
            e.printStackTrace();
            cnmApiClientCallBack.onError(e);
        }
        cnmApiClient.getOrganisationUnitTree(cnmApiClientCallBack);
    }
}
