package org.eyeseetea.malariacare.data.sync.importer.strategies;

import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.sync.importer.OrgUnitToOptionConverter;
import org.eyeseetea.malariacare.data.sync.importer.models.OrganisationUnitExtended;

import java.util.List;

public abstract class AOrgUnitToOptionConverterStrategy {

    public void convert(List<OrganisationUnitExtended> organisationUnitExtendeds) {
    }

    public void convert() {
        List<QuestionDB> questionDBs = QuestionDB.getAllQuestionsWithOrgUnitDropdownList();

        if (questionDBs.size() == 0) {
            return;
        }

        List<OrgUnitDB> orgUnitDBs = OrgUnitDB.getAllOrgUnit();
        for (OrgUnitDB orgUnitDB : orgUnitDBs) {
            OrgUnitToOptionConverter.addOUOptionToQuestions(questionDBs, orgUnitDB);
        }
    }

    public void convertFromList(List<OrganisationUnitExtended> organisationUnitExtendeds) {
    }
}
