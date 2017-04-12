package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.data.database.model.Survey;
import org.eyeseetea.malariacare.data.database.utils.SurveyAnsweredRatioCache;
import org.eyeseetea.malariacare.domain.entity.SurveyAnsweredRatio;

public class GetSurveyAnsweredRatioUseCase {
    private Survey mSurvey;

    public GetSurveyAnsweredRatioUseCase(Survey survey) {
        mSurvey = survey;
    }

    public void execute() {
        SurveyAnsweredRatio answeredQuestionRatio = SurveyAnsweredRatioCache.get(
                mSurvey.getId_survey());
        if (answeredQuestionRatio == null) {
            AReloadSurveyAnsweredRatioUseCase
                    reloadSurveyUseCase = new ReloadSurveyAnsweredRatioUseCase(mSurvey);
            reloadSurveyUseCase.execute();
        }
    }
}
