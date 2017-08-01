package org.eyeseetea.malariacare.domain.usecase.strategies;

import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IValueRepository;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.domain.usecase.AGetReviewValuesBySurveyIdUseCaseStrategy;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationController;

import java.util.ArrayList;
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
        List<Value> orderedList = new ArrayList<>();
        //TODO refactor navigation controller to be in domain layer
        NavigationController navigationController = Session.getNavigationController();
        navigationController.first();
        String uidNextQuestion = null;
        do {
            for (Value value : values) {
                if (value.getQuestionUId() != null) {
                    if (value.getQuestionUId().equals(
                            navigationController.getCurrentQuestion().getUid())) {
                        orderedList.add(value);
                        uidNextQuestion = navigationController.next(
                                Option.findByCode(value.getOptionCode())).getUid();
                    }
                }
            }
        } while (uidNextQuestion != null);
        return orderedList;
    }
}
