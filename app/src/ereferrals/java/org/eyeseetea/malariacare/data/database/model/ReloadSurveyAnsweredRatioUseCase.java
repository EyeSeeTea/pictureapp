package org.eyeseetea.malariacare.data.database.model;

import org.eyeseetea.malariacare.domain.usecase.AReloadSurveyAnsweredRatioUseCase;

public class ReloadSurveyAnsweredRatioUseCase extends AReloadSurveyAnsweredRatioUseCase {
    public ReloadSurveyAnsweredRatioUseCase(Survey survey) {
        super(survey);
    }

    @Override
    public void execute() {
        reloadMalariaSurveyAnsweredRatio();
    }
}
