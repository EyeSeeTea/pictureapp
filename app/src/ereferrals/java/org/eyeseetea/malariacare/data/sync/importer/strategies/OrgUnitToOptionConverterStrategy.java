package org.eyeseetea.malariacare.data.sync.importer.strategies;


import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.sync.importer.models.OrganisationUnitExtended;

import java.util.List;

public class OrgUnitToOptionConverterStrategy extends AOrgUnitToOptionConverterStrategy {

    @Override
    public void convert(List<OrganisationUnitExtended> organisationUnitExtendeds) {
        convertFromList(organisationUnitExtendeds);
    }

    public void convertFromList(List<OrganisationUnitExtended> organisationUnitExtendeds) {

    }

    private static void addOUOptionToQuestions(List<QuestionDB> questionDBs, OrgUnitDB orgUnitDB) {

    }

    private static boolean existsOrgUnitAsOptionInQuestion(OrgUnitDB orgUnitDB,
            QuestionDB questionDB) {


        return false;
    }

}