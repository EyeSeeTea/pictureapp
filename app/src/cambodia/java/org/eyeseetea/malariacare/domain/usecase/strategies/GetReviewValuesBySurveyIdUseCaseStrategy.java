package org.eyeseetea.malariacare.domain.usecase.strategies;

import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.service.OrderValuesByQuestionDomainService;

import java.util.List;


public class GetReviewValuesBySurveyIdUseCaseStrategy extends
        AGetReviewValuesBySurveyIdUseCaseStrategy {

    @Override
    public List<Value> orderValues(List<Value> values) {
        return OrderValuesByQuestionDomainService.execute(values);
    }
}
