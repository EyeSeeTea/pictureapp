package org.eyeseetea.malariacare.domain.usecase.strategies;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationController;

import java.util.ArrayList;
import java.util.List;


public class GetReviewValuesBySurveyIdUseCaseStrategy extends
        AGetReviewValuesBySurveyIdUseCaseStrategy {

    @Override
    public List<Value> orderValues(List<Value> values) {
        List<Value> orderedList = new ArrayList<>();
        //TODO refactor navigation controller to be in domain layer (issue 1354)
        NavigationController navigationController = Session.getNavigationController();
        navigationController.first();
        String uidNextQuestion = null;
        do {
            QuestionDB currentQuestion = navigationController.getCurrentQuestion();
            if (TabDB.isMultiQuestionTab(currentQuestion.getHeaderDB().getTabDB()
                    .getType())) {
                List<QuestionDB> tabQuestionDBs = currentQuestion.getQuestionsByTab(
                        currentQuestion.getHeaderDB()
                                .getTabDB());
                for (QuestionDB questionDB : tabQuestionDBs) {
                    for (Value value : values) {
                        if (value.getQuestionUId().equals(questionDB.getUid())) {
                            orderedList.add(value);
                            break;
                        }
                    }
                }
                //TODO put uidNextQuestion=navigationController.next() not working with this now
                uidNextQuestion = null;
            } else {
                for (Value value : values) {
                    if (value.getQuestionUId() != null) {
                        if (value.getQuestionUId().equals(currentQuestion.getUid())) {
                            orderedList.add(value);
                            uidNextQuestion = navigationController.next(
                                    OptionDB.findByCode(value.getOptionCode())).getUid();
                        }
                    }
                }
            }
        } while (uidNextQuestion != null);
        return orderedList;
    }
}

