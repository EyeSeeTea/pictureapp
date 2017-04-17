package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.data.database.model.Survey;

public class ReloadSurveyAnsweredRatioUseCase extends AReloadSurveyAnsweredRatioUseCase {
    public ReloadSurveyAnsweredRatioUseCase(Survey survey) {
        super(survey);
    }

    @Override
    public void execute() {
        reloadMalariaSurveyAnsweredRatio();
    }

}
