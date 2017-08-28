package org.eyeseetea.malariacare.domain.usecase.strategies;

import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.SurveysThresholds;
import org.eyeseetea.malariacare.domain.usecase.push.strategies.APushUseCaseStrategy;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

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

        List<Survey> surveyList = mSurveyRepository.getAllQuarantineSurveys();
        for(Survey survey : surveyList){
            survey.setStatus(Constants.SURVEY_COMPLETED);
            mSurveyRepository.save(survey);
        }

        if (pushController.isPushInProgress()) {
            notifyPushInProgressError();
        } else {
            pushController.changePushInProgress(true);
            runPush();
        }
    }
}
