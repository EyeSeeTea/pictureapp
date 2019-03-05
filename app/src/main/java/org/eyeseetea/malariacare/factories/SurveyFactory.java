package org.eyeseetea.malariacare.factories;

import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.usecase.DeleteSurveyByUidUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetSurveysByProgram;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

public class SurveyFactory {
    protected IMainExecutor mainExecutor = new UIThreadExecutor();
    protected IAsyncExecutor asyncExecutor = new AsyncExecutor();

    ISurveyRepository surveyRepository = new SurveyLocalDataSource();

    public GetSurveysByProgram getSurveysUseCaseByprogram() {
        GetSurveysByProgram getSurveysByProgram =
                new GetSurveysByProgram(asyncExecutor, mainExecutor, surveyRepository);

        return getSurveysByProgram;
    }

    public DeleteSurveyByUidUseCase deleteSurveyByUidUseCase() {
        DeleteSurveyByUidUseCase deleteSurveyByUidUseCase =
                new DeleteSurveyByUidUseCase(asyncExecutor, mainExecutor, surveyRepository);

        return deleteSurveyByUidUseCase;
    }
}
