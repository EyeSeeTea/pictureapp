package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;

public class DeleteOrgUnitUseCase implements UseCase {
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private IOrganisationUnitRepository mOrganisationUnitRepository;
    private Callback mCallback;

    public DeleteOrgUnitUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IOrganisationUnitRepository organisationUnitRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mOrganisationUnitRepository = organisationUnitRepository;
    }

    public void excute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        mOrganisationUnitRepository.removeCurrentOrganisationUnit();
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onSuccess();
            }
        });
    }

    public interface Callback {
        void onSuccess();
    }
}
