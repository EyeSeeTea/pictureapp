package org.eyeseetea.malariacare.data.database.utils.populatedb.strategies;

public class PopulateRowStartegy extends APopulateRowStrategy {

    @Override
    public TreatmentDB populateTreatments(String[] line, HashMap<Long, PartnerDB> organisationFK,
            @Nullable TreatmentDB treatmentDB) {
        if (treatmentDB == null) {
            treatmentDB = new TreatmentDB();
        }
        treatmentDB.setOrganisation(organisationFK.get(Long.parseLong(line[1])));
        treatmentDB.setDiagnosis(stringKeyList.get(line[2]).getId_string_key());
        treatmentDB.setMessage(stringKeyList.get(line[3]).getId_string_key());
        treatmentDB.setType(Integer.parseInt(line[4]));
        return treatmentDB;
    }
}
