package org.eyeseetea.malariacare.data.sync.importer.strategies;

import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.List;

public class OrgUnitToOptionConverterStrategy extends AOrgUnitToOptionConverterStrategy {

    public static void addOUOptionToQuestions(List<QuestionDB> questionDBs, OrgUnitDB orgUnitDB) {
        // orgUnitDB
        for (QuestionDB questionDB : questionDBs) {
            if (!existsOrgUnitAsOptionInQuestion(orgUnitDB, questionDB)) {
                OptionDB optionDB = new OptionDB();
                optionDB.setAnswerDB(questionDB.getAnswerDB());
                optionDB.setCode(orgUnitDB.getUid());
                optionDB.setName(orgUnitDB.getName());
                OptionAttributeDB optionAttributeDB = new OptionAttributeDB();
                optionAttributeDB.setPath(orgUnitDB.getCoordinates());
                optionAttributeDB.save();
                optionDB.setOptionAttributeDB(optionAttributeDB);
                optionDB.save();
            }
        }
    }

    private static boolean existsOrgUnitAsOptionInQuestion(OrgUnitDB orgUnitDB,
            QuestionDB questionDB) {
        List<OptionDB> optionDBs = questionDB.getAnswerDB().getOptionDBs();

        for (OptionDB optionDB : optionDBs) {
            if (optionDB.getCode().equals(orgUnitDB.getUid())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void convert() {
        List<QuestionDB> questionDBs = QuestionDB.getAllQuestionsWithOutput(
                Constants.DROPDOWN_OU_LIST);

        if (questionDBs.size() != 0) {
            List<OrgUnitDB> orgUnitDBs = OrgUnitDB.getAllOrgUnit();
            for (OrgUnitDB orgUnitDB : orgUnitDBs) {
                addOUOptionToQuestions(questionDBs, orgUnitDB);
            }
        }
    }
}
