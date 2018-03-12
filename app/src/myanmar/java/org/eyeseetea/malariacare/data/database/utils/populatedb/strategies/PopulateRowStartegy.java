package org.eyeseetea.malariacare.data.database.utils.populatedb.strategies;

public class PopulateRowStartegy extends APopulateRowStrategy {

    @Override
    public TreatmentDB populateTreatments(String[] line, HashMap<Long, PartnerDB> organisationFK,
             @Nullable TreatmentDB treatmentDB) {
        if (treatmentDB == null) {
            treatmentDB = new TreatmentDB();
        }
        treatmentDB.setOrganisation(organisationFK.get(Long.parseLong(line[1])));
        treatmentDB.setDiagnosis(line[2]);
        treatmentDB.setMessage(line[3]);
        treatmentDB.setType(Integer.parseInt(line[4]));
        return treatmentDB;
    }
}
