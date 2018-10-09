package org.eyeseetea.malariacare.domain.usecase.strategies;

import org.eyeseetea.malariacare.data.database.datasources.UserAccountDataSource;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.UserAccount;
import org.eyeseetea.malariacare.domain.exception.ApiCallException;
import org.eyeseetea.malariacare.domain.exception.ConfigFileObsoleteException;
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.SurveysThresholds;
import org.eyeseetea.malariacare.domain.usecase.push.strategies.APushUseCaseStrategy;

public class PushUseCaseStrategy extends APushUseCaseStrategy {
    public PushUseCaseStrategy(IPushController pushController,
            IOrganisationUnitRepository organisationUnitRepository,
            SurveysThresholds surveysThresholds,
            ISurveyRepository surveyRepository,
            IMainExecutor mainExecutor) {
        super(pushController, organisationUnitRepository, surveysThresholds, surveyRepository,
                mainExecutor);
    }

    @Override
    public void run(PushUseCase.Callback callback) {
        mCallback = callback;
        IPushController pushController = mPushController;
        if (pushController.isPushInProgress()) {
            notifyPushInProgressError();
        } else {
            pushController.changePushInProgress(true);
            runPush();
        }
    }

    @Override
    protected void treatApiCallException(ApiCallException e) {
        super.treatApiCallException(e);
        if (e instanceof ConfigFileObsoleteException) {
            disableAddNewSurveys();
        }
    }

    private void disableAddNewSurveys() {
        IUserRepository userRepository = new UserAccountDataSource();
        UserAccount userAccount = userRepository.getLoggedUser();
        userAccount.setCanAddSurveys(false);
        userRepository.saveLoggedUser(userAccount);
    }
}
