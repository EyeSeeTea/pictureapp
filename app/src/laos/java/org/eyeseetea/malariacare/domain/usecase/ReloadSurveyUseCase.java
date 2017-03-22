package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.data.database.model.Survey;

public class ReloadSurveyAnsweredRatioUseCase extends AReloadSurveyUseCase {
    public ReloadSurveyUseCase(Survey survey) {
        super(survey);
    }

    @Override
    public void execute() {
        reloadMalariaSurveyAnsweredRatio();
    }

}
