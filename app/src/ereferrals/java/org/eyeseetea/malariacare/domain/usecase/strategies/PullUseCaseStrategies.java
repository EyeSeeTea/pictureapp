package org.eyeseetea.malariacare.domain.usecase.strategies;

import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.importer.PullOrganisationCredentialsController;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.domain.usecase.pull.strategies.APullUseCaseStrategy;


public class PullUseCaseStrategies extends APullUseCaseStrategy {

    private PullOrganisationCredentialsController mPullOrganisationCredentialsController;

    public PullUseCaseStrategies(
            PullUseCase pullUseCase) {
        super(pullUseCase);
        mPullOrganisationCredentialsController = new PullOrganisationCredentialsController(
                PreferencesEReferral.getUserCredentialsFromPreferences(),
                PreferencesState.getInstance().getContext());
    }

    @Override
    public void onPullComplete() {
        mPullUseCase.notifyComplete();
    }

    @Override
    public void onOnNetworkError() {
        if (PreferencesEReferral.getUserProgramId() != -1) {
            mPullUseCase.notifyComplete();
        } else {
            mPullUseCase.notifyOnNetworkError();
        }
    }
}
