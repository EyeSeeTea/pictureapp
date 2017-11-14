package org.eyeseetea.malariacare.data.sync.importer.strategies;

import static org.hisp.dhis.client.sdk.android.api.persistence.flow.OrganisationUnitFlow.MAPPER;

import android.content.Context;
import android.util.Log;

import com.raizlabs.android.dbflow.structure.Model;

import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.data.database.datasources.AppInfoDataSource;
import org.eyeseetea.malariacare.data.database.datasources.DeviceDataSource;
import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.utils.DatabaseUtils;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.remote.datasource.AppInfoRemoteDataSource;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.sync.importer.CnmApiClient;
import org.eyeseetea.malariacare.data.sync.importer.ConvertFromApiVisitor;
import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.data.sync.importer.models.OrgUnitTree;
import org.eyeseetea.malariacare.data.sync.importer.models.OrganisationUnitExtended;
import org.eyeseetea.malariacare.domain.AutoconfigureException;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IDeviceRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ReadPolicy;
import org.eyeseetea.malariacare.domain.entity.AppInfo;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.entity.OrganisationUnit;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ConfigJsonIOException;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.organisationunit
        .ExistsMoreThanOneOrgUnitByPhoneException;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.eyeseetea.malariacare.network.ServerAPIController;
import org.eyeseetea.malariacare.utils.Constants;
import org.hisp.dhis.client.sdk.android.api.D2;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.OrganisationUnitFlow;
import org.hisp.dhis.client.sdk.core.common.controllers.SyncStrategy;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class PullControllerStrategy extends APullControllerStrategy {

    IOrganisationUnitRepository organisationUnitRepository;
    IDeviceRepository deviceRepository;
    AuthenticationManager authenticationManager;
    IAppInfoRepository appInfoDataSource = new AppInfoDataSource();
    PullFilters mPullFilters;
    IAppInfoRepository appInfoRemoteDataSource = new AppInfoRemoteDataSource();

    public PullControllerStrategy(PullController pullController) {
        super(pullController);
    }

    @Override
    public void pull(PullFilters pullFilters, IPullController.Callback callback, Context context) {
        Log.d(TAG, "Starting PULL process...");
        mPullFilters = pullFilters;
        try {

            callback.onStep(PullStep.METADATA);
            mPullController.populateMetadataFromCsvs(pullFilters.isAutoConfig());

            if (pullFilters.isAutoConfig()) {
                autoConfigureByPhone(context, callback, pullFilters);
            } else {
                downloadMetadata(pullFilters, callback);
            }

        } catch (Exception ex) {
            Log.e(TAG, "pull: " + ex.getLocalizedMessage());
            ex.printStackTrace();
            callback.onError(ex);
        }
    }

    private void autoConfigureByPhone(Context context,
            final IPullController.Callback callback, final PullFilters pullFilters)
            throws NetworkException, ApiCallException, AutoconfigureException {

        organisationUnitRepository = new OrganisationUnitRepository();
        deviceRepository = new DeviceDataSource();
        authenticationManager = new AuthenticationManager(context);

        if (isOrgUnitConfigured()) {
            downloadMetadata(pullFilters, callback);
        } else {
            callback.onStep(PullStep.AUTO_CONFIGURE_ORG_UNIT);
            OrganisationUnit organisationUnit = getOrganisationUnitByPhone(callback);

            if (organisationUnit == null) {
                throw new AutoconfigureException();
            } else {
                organisationUnitRepository.saveCurrentOrganisationUnit(organisationUnit);
                pullFilters.setDataByOrgUnit(organisationUnit.getName());

                AppInfo appInfo = appInfoDataSource.getAppInfo();
                appInfo.setMetadataDownloaded(false);
                appInfoDataSource.saveAppInfo(appInfo);

                Credentials hardcodedCredentials = getHardcodedCredentials(callback);

                if (hardcodedCredentials != null) {
                    authenticationManager.login(hardcodedCredentials,
                            new IAuthenticationManager.Callback<UserAccount>() {
                                @Override
                                public void onSuccess(UserAccount result) {
                                    Log.d(TAG, "Create autoconfigured user");
                                    downloadMetadata(pullFilters, callback);
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
    }

    private OrganisationUnit getOrganisationUnitByPhone(IPullController.Callback callback)
            throws ApiCallException {
        OrganisationUnit organisationUnit = null;
        try {
            organisationUnit = organisationUnitRepository.getOrganisationUnitByPhone(
                    deviceRepository.getDevice());
        } catch (ExistsMoreThanOneOrgUnitByPhoneException
                existsMoreThanOneOrgUnitByPhoneException) {
            organisationUnit =
                    existsMoreThanOneOrgUnitByPhoneException.getSelectedOrganisationUnit();
            callback.onWarning(existsMoreThanOneOrgUnitByPhoneException);
        }

        return organisationUnit;
    }

    private boolean isOrgUnitConfigured() throws NetworkException, ApiCallException {
        OrganisationUnit organisationUnit = organisationUnitRepository.getCurrentOrganisationUnit(
                ReadPolicy.CACHE);

        return (organisationUnit != null);
    }

    private void downloadMetadata(final PullFilters pullFilters,
            final IPullController.Callback callback) {
        appInfoRemoteDataSource.getAppInfo(new IDataSourceCallback<AppInfo>() {
            @Override
            public void onSuccess(AppInfo appInfoRemote) {
                AppInfo appInfoLocal = appInfoDataSource.getAppInfo();
                if (pullFilters.pullMetaData() &&
                        Integer.parseInt(appInfoLocal.getMetadataVersion()) < Integer.parseInt(
                        appInfoRemote.getMetadataVersion())) {
                    appInfoLocal.setMetadataDownloaded(false);
                    appInfoLocal.setMetadataVersion(appInfoRemote.getMetadataVersion());
                    appInfoDataSource.saveAppInfo(appInfoLocal);
                    deleteObsoleteMetadata();
                }

                if (pullFilters.isDemo() || appInfoLocal.isMetadataDownloaded()) {
                    callback.onComplete();
                } else {
                    mPullController.pullMetada(pullFilters, callback);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
            }
        });

    }

    private void deleteObsoleteMetadata() {
        QuestionDB orgUnitTreeQuestion = QuestionDB.getAllQuestionsWithOutput(
                Constants.DROPDOWN_LIST_OU_TREE).get(0);
        List<Model> optionAttributes = new ArrayList<Model>(
                OptionAttributeDB.getOptionAttributesFromQuestion(
                        orgUnitTreeQuestion));
        List<Model> options = new ArrayList<Model>(
                QuestionDB.getOptions(orgUnitTreeQuestion));
        DatabaseUtils.deleteBatch(optionAttributes);
        DatabaseUtils.deleteBatch(options);
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
                Log.d(TAG, "Converting orgUnitTree...");
                ConvertFromApiVisitor convertFromApiVisitor = new ConvertFromApiVisitor();
                OrgUnitTree orgUnitTree = new OrgUnitTree();
                orgUnitTree.accept(convertFromApiVisitor, result);

                AppInfo appInfo = appInfoDataSource.getAppInfo();
                boolean isActiveOu = mPullFilters.getDataByOrgUnit()!=null && !mPullFilters.getDataByOrgUnit().equals("");
                appInfo.setMetadataDownloaded(isActiveOu);
                appInfoDataSource.saveAppInfo(appInfo);

                callback.onComplete();
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

    @Override
    public void pullAndCovertOuOptions(final String selectedUid,
            final IPullController.Callback callback) {
        D2.organisationUnits().pullAllDescendants(SyncStrategy.NO_STORE, selectedUid).subscribeOn(
                Schedulers.io()).
                observeOn(Schedulers.io()).toBlocking()
                .subscribe(new Action1<List<org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit>>() {
                    @Override
                    public void call(List<org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit> organisationUnitsDescendants) {
                        List<OrganisationUnitExtended> organisationUnitExtendeds =
                                new ArrayList<>();
                        if (organisationUnitsDescendants != null
                                && organisationUnitsDescendants.size() > 0) {
                            for (OrganisationUnitFlow organisationUnitDescendant
                                    : MAPPER.mapToDatabaseEntities(
                                    organisationUnitsDescendants)) {
                                if (!organisationUnitDescendant.getUId().equals(selectedUid)) {
                                    OrganisationUnitExtended
                                            organisationUnitExtended =
                                            new OrganisationUnitExtended(
                                                    organisationUnitDescendant);
                                    organisationUnitExtendeds.add(
                                            organisationUnitExtended);
                                }
                            }
                            new OrgUnitToOptionConverterStrategy().convert(organisationUnitExtendeds);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        callback.onError(throwable);
                    }
                });
    }
}
