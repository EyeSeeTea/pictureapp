package org.eyeseetea.malariacare.domain.service;

import org.eyeseetea.malariacare.domain.usecase.push.SurveysThresholds;

public class SurveysThresholdBuilder {

    private int mCount = 0;
    private int mTimeHour = 1;

    public SurveysThresholdBuilder withCount(int count) {
        mCount = count;

        return this;
    }

    public SurveysThresholdBuilder withTimeHour(int time) {
        mTimeHour = time;

        return this;
    }

    public SurveysThresholds build() {
        SurveysThresholds surveysThreshold = new SurveysThresholds(mCount, mTimeHour);

        return surveysThreshold;
    }
}
