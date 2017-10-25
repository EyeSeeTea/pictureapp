package org.eyeseetea.malariacare.data.sync.importer.strategies;

import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.sync.importer.ConvertFromSDKVisitor;
import org.eyeseetea.malariacare.data.sync.importer.models.OrganisationUnitExtended;

import java.util.List;

public class OrgUnitToOptionConverterStrategy extends AOrgUnitToOptionConverterStrategy {

    @Override
    public void convert(List<OrganisationUnitExtended> organisationUnitExtendeds) {
        convertFromList(organisationUnitExtendeds);
    }

    public void convertFromList(List<OrganisationUnitExtended> organisationUnitExtendeds) {
        List<QuestionDB> questionDBs = QuestionDB.getAllQuestionsWithOrgUnitDropdownList();

        if (questionDBs.size() == 0) {
            return;
        }

        for (OrganisationUnitExtended organisationUnitExtended : organisationUnitExtendeds) {
            OrgUnitDB temporalOrgUnitDB = ConvertFromSDKVisitor.convertOrganisationUnitExtended(
                    organisationUnitExtended);
            addOUOptionToQuestions(questionDBs, temporalOrgUnitDB);
        }
    }

    private static void addOUOptionToQuestions(List<QuestionDB> questionDBs, OrgUnitDB orgUnitDB) {
        // orgUnitDB
        for (QuestionDB questionDB : questionDBs) {
            if (!existsOrgUnitAsOptionInQuestion(orgUnitDB, questionDB)) {
                OptionDB optionDB = new OptionDB();
                optionDB.setAnswerDB(questionDB.getAnswerDB());
                optionDB.setCode(orgUnitDB.getName());
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
            if (optionDB.getCode().equals(orgUnitDB.getName())) {
                return true;
            }
        }

        return false;
    }

}
