package org.eyeseetea.malariacare.data.database.utils.populatedb.strategies;

import android.support.annotation.Nullable;

import org.eyeseetea.malariacare.data.database.model.PartnerDB;
import org.eyeseetea.malariacare.data.database.model.TreatmentDB;

import java.util.HashMap;

public class PopulateRowStrategy extends APopulateRowStrategy {

    @Override
    public TreatmentDB populateTreatments(String[] line, HashMap<Long, PartnerDB> organisationFK,
            @Nullable TreatmentDB treatmentDB) {
        if (treatmentDB == null) {
            treatmentDB = new TreatmentDB();
        }
        treatmentDB.setPartnerDB(Long.parseLong(line[1]));
        treatmentDB.setDiagnosis(line[2]);// string_key
        treatmentDB.setMessage(line[3]);// string_key
        treatmentDB.setType(Integer.parseInt(line[4]));
        return treatmentDB;
    }
}
