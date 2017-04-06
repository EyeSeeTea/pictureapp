package org.eyeseetea.malariacare.domain.usecase.strategies;

import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.sync.importer.PullOrganisationCredentialsController;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
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
        mPullOrganisationCredentialsController.pullUserProgram(
                new PullOrganisationCredentialsController.Callback() {
                    @Override
                    public void onComplete() {
                        mPullUseCase.notifyComplete();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        if (throwable instanceof NetworkException) {
                            mPullUseCase.notifyOnNetworkError();
                        } else if (throwable instanceof PullConversionException) {
                            mPullUseCase.notifyPullConversionError();
                        } else {
                            mPullUseCase.notifyError(throwable.getMessage());
                        }
                    }
                });
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
