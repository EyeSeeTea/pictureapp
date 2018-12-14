package org.eyeseetea.malariacare.factories;

import android.content.Context;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.data.database.datasources.SurveyLocalDataSource;
import org.eyeseetea.malariacare.data.repositories.OrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.IPullController;
import org.eyeseetea.malariacare.domain.boundary.IPushController;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOrganisationUnitRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISurveyRepository;
import org.eyeseetea.malariacare.domain.usecase.pull.PullUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.PushUseCase;
import org.eyeseetea.malariacare.domain.usecase.push.SurveysThresholds;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

public abstract class ASyncFactory {
    protected IMainExecutor mainExecutor = new UIThreadExecutor();
    protected IAsyncExecutor asyncExecutor = new AsyncExecutor();

    protected abstract IPullController getPullController(Context context);

    protected abstract IPushController getPushController(Context context);

    public PullUseCase getPullUseCase(Context context) {
        IPullController pullController = getPullController(context);
        return new PullUseCase(pullController, asyncExecutor, mainExecutor);
    }

    public PushUseCase getPushUseCase(Context context) {
        IPushController pushController = getPushController(context);
        ISurveyRepository surveyRepository = new SurveyLocalDataSource();
        IOrganisationUnitRepository orgUnitRepository = new OrganisationUnitRepository();

        SurveysThresholds surveysThresholds =
                new SurveysThresholds(BuildConfig.LimitSurveysCount,
                        BuildConfig.LimitSurveysTimeHours);

        return new PushUseCase(pushController, asyncExecutor, mainExecutor,
                surveysThresholds, surveyRepository, orgUnitRepository);

    }
}
