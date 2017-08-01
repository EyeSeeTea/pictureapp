package org.eyeseetea.malariacare.domain.usecase.strategies;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IValueRepository;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.usecase.AGetReviewValuesBySurveyIdUseCaseStrategy;

import java.util.List;


public class GetReviewValuesBySurveyIdUseCaseStrategy extends
        AGetReviewValuesBySurveyIdUseCaseStrategy {
    public GetReviewValuesBySurveyIdUseCaseStrategy(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IValueRepository IValueRepository) {
        super(mainExecutor, asyncExecutor, IValueRepository);
    }

    @Override
    protected List<Value> orderValues(List<Value> values) {
        return values;
    }
}
