package org.eyeseetea.malariacare.data.sync.importer;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.Question;

import java.util.List;

public class OrgUnitToOptionConverter {
    public static void convert() {
        List<Question> questions = Question.getAllQuestionsWithOrgUnitDropdownList();

        if (questions.size() == 0) {
            return;
        }

        List<OrgUnitDB> orgUnitDBs = OrgUnitDB.getAllOrgUnit();
        for (OrgUnitDB orgUnitDB : orgUnitDBs) {
            addOUOptionToQuestions(questions, orgUnitDB);
        }
    }

    public static void addOUOptionToQuestions(List<Question> questions, OrgUnitDB orgUnitDB) {
        for (Question question : questions) {
            if (!existsOrgUnitAsOptionInQuestion(orgUnitDB, question)) {
                OptionDB optionDB = new OptionDB();
                optionDB.setAnswerDB(question.getAnswerDB());
                optionDB.setCode(orgUnitDB.getUid());
                optionDB.setName(orgUnitDB.getName());
                optionDB.save();
            }
        }
    }

    private static boolean existsOrgUnitAsOptionInQuestion(OrgUnitDB orgUnitDB, Question question) {
        List<OptionDB> optionDBs = question.getAnswerDB().getOptionDBs();

        for (OptionDB optionDB : optionDBs) {
            if (optionDB.getCode().equals(orgUnitDB.getUid())) {
                return true;
            }
        }

        return false;
    }
}
