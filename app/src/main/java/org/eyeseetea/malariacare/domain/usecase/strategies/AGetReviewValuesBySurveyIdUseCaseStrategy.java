package org.eyeseetea.malariacare.domain.usecase.strategies;

import org.eyeseetea.malariacare.domain.entity.Value;

import java.util.List;

public abstract class AGetReviewValuesBySurveyIdUseCaseStrategy {

    public abstract List<Value> orderValues(List<Value> values);
}
