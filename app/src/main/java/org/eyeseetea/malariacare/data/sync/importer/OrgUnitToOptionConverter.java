package org.eyeseetea.malariacare.data.sync.importer;

import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.QuestionOption;

import java.util.List;

public class OrgUnitToOptionConverter {
    public static void convert() {
        List<Question> questions = Question.getAllQuestionsWithOrgUnitDropdownList();
        //remove older values, but not the especial "other" option
        for (Question question : questions) {
            List<Option> options = question.getAnswer().getOptions();
            removeOldValues(question, options);
        }

        if (questions.size() == 0) {
            return;
        }

        //Generate the orgUnits options for each question with orgunit dropdown list
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
                option.setName(orgUnit.getUid());
                option.setCode(orgUnit.getName());
                option.save();
            }
        }
    }

    private static boolean existsOrgUnitAsOptionInQuestion(OrgUnit orgUnit, Question question) {
        List<Option> options = question.getAnswer().getOptions();

        for (Option option : options) {
            if (option.getName().equals(orgUnit.getUid())) {
                return true;
            }
        }

        return false;
    }

    public static void removeOldValues(Question question, List<Option> options) {
        for (Option option : options) {
            if (QuestionOption.findByQuestionAndOption(question, option).size() == 0) {
                option.delete();
            }
        }
    }
}
