package org.eyeseetea.malariacare.domain.service;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.utils.Session;
import org.eyeseetea.malariacare.domain.entity.Value;
import org.eyeseetea.malariacare.layout.adapters.survey.navigation.NavigationController;

import java.util.ArrayList;
import java.util.List;

public class OrderValuesByQuestionDomainService {
    public static List<Value> execute(List<Value> values) {
        List<Value> orderedList = new ArrayList<>();
        //TODO refactor navigation controller to be in domain layer (issue 1354)
        NavigationController navigationController = Session.getNavigationController();
        navigationController.first();
        String uidNextQuestion = null;
        do {
            for (Value value : values) {
                if (value.getQuestionUId() != null) {
                    if (value.getQuestionUId().equals(
                            navigationController.getCurrentQuestion().getUid())) {
                        orderedList.add(value);
                        QuestionDB nextQuestion = navigationController.next(
                                OptionDB.findByCode(value.getOptionCode()));

                        if (nextQuestion != null) {
                            uidNextQuestion = nextQuestion.getUid();
                        } else {
                            uidNextQuestion = null;
                        }
                    }
                }
            }
        } while (uidNextQuestion != null);
        return orderedList;
    }
}
