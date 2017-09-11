package org.eyeseetea.malariacare.data.sync.importer;

import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.model.Question;

import java.util.List;

public class OrgUnitToOptionConverter {
    public static void convert() {
        List<Question> questions = Question.getAllQuestionsWithOrgUnitDropdownList();

        if (questions.size() == 0) {
            return;
        }

        List<OrgUnit> orgUnits = OrgUnit.getAllOrgUnit();
        for (OrgUnit orgUnit : orgUnits) {
            addOUOptionToQuestions(questions, orgUnit);
        }
    }

    public static void addOUOptionToQuestions(List<Question> questions, OrgUnit orgUnit) {
        for (Question question : questions) {
            if (!existsOrgUnitAsOptionInQuestion(orgUnit, question)) {
                Option option = new Option();
                option.setAnswer(question.getAnswer());
                option.setCode(orgUnit.getUid());
                option.setName(orgUnit.getName());
                option.save();
            }
        }
    }

    private static boolean existsOrgUnitAsOptionInQuestion(OrgUnit orgUnit, Question question) {
        List<Option> options = question.getAnswer().getOptions();

        for (Option option : options) {
            if (option.getCode().equals(orgUnit.getUid())) {
                return true;
            }
        }

        return false;
    }
}
