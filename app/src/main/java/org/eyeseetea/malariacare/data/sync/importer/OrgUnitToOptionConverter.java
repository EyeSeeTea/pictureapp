package org.eyeseetea.malariacare.data.sync.importer;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;

import java.util.List;

public class OrgUnitToOptionConverter {
    public static void convert() {
        List<QuestionDB> questionDBs = QuestionDB.getAllQuestionsWithOrgUnitDropdownList();

        if (questionDBs.size() == 0) {
            return;
        }

        List<OrgUnitDB> orgUnitDBs = OrgUnitDB.getAllOrgUnit();
        for (OrgUnitDB orgUnitDB : orgUnitDBs) {
            addOUOptionToQuestions(questionDBs, orgUnitDB);
        }
    }

    public static void addOUOptionToQuestions(List<QuestionDB> questionDBs, OrgUnitDB orgUnitDB) {
        for (QuestionDB questionDB : questionDBs) {
            if (!existsOrgUnitAsOptionInQuestion(orgUnitDB, questionDB)) {
                OptionDB optionDB = new OptionDB();
                optionDB.setAnswerDB(questionDB.getAnswerDB());
                optionDB.setCode(orgUnitDB.getUid());
                optionDB.setName(orgUnitDB.getName());
                optionDB.save();
            }
        }
    }

    private static boolean existsOrgUnitAsOptionInQuestion(OrgUnitDB orgUnitDB, QuestionDB questionDB) {
        List<OptionDB> optionDBs = questionDB.getAnswerDB().getOptionDBs();

        for (OptionDB optionDB : optionDBs) {
            if (optionDB.getCode().equals(orgUnitDB.getUid())) {
                return true;
            }
        }

        return false;
    }
}
