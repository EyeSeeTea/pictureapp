package org.eyeseetea.malariacare.data.sync.importer.strategies;

import android.util.Log;

import org.eyeseetea.malariacare.data.database.CredentialsLocalDataSource;
import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.remote.SdkQueries;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.PullController;
import org.eyeseetea.malariacare.data.sync.importer.models.CategoryOptionGroupExtended;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.repositories.ICredentialsRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.usecase.pull.PullFilters;
import org.eyeseetea.malariacare.domain.usecase.pull.PullStep;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.CategoryOptionGroupFlow;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.ArrayList;

public class PullControllerStrategy extends APullControllerStrategy {
    public PullControllerStrategy(PullController pullController) {
        super(pullController);
    }

    @Override
    public void convertMetadata(ConvertFromSDKVisitor converter) {
        for (CategoryOptionGroupFlow categoryOptionGroupFlow : SdkQueries.getCategoryOptionGroups
                ()) {
            CategoryOptionGroupExtended categoryOptionGroupExtended =
                    new CategoryOptionGroupExtended(categoryOptionGroupFlow);
            categoryOptionGroupExtended.accept(converter);
        }
    }

    @Override
    public void pull(PullFilters pullFilters, IPullController.Callback callback) {
        Log.d(TAG, "Starting PULL process...");
        callback.onStep(PullStep.METADATA);

        try {
            mPullController.populateMetadataFromCsvs(pullFilters.isDemo());
            mPullController.pullData(pullFilters, new ArrayList<OrganisationUnit>(), callback);
        } catch (Exception ex) {
            Log.e(TAG, "pull: " + ex.getLocalizedMessage());
            callback.onError(ex);
        }
    }

    @Override
    public void onPullDataComplete(final IPullController.Callback callback) {
        ICredentialsRepository credentialsLocalDataSource = new CredentialsLocalDataSource();
        IOrganisationUnitRepository orgUnitDataSource = new OrganisationUnitRepository();
        IProgramRepository programLocalDataSource = new ProgramLocalDataSource();
        try {
            org.eyeseetea.malariacare.domain.entity.OrganisationUnit orgUnit =
                    orgUnitDataSource.getUserOrgUnit(
                            credentialsLocalDataSource.getOrganisationCredentials());
            org.eyeseetea.malariacare.domain.entity.Program program = orgUnit.getProgram();
            programLocalDataSource.saveUserProgramId(program);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }
        mPullController.convertData(callback);
    }
}
