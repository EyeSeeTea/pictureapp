package org.eyeseetea.malariacare.data.database.utils.populatedb.strategies;

import android.support.annotation.Nullable;

import org.eyeseetea.malariacare.data.database.model.PartnerDB;
import org.eyeseetea.malariacare.data.database.model.StringKeyDB;
import org.eyeseetea.malariacare.data.database.model.TreatmentDB;

import java.util.HashMap;

public class PopulateRowStrategy extends APopulateRowStrategy {

    @Override
    public  TreatmentDB populateTreatments(String[] line, HashMap<Long, PartnerDB> organisationFK,
            HashMap<Long, StringKeyDB> stringKeyList, @Nullable TreatmentDB treatmentDB) {
        if (treatmentDB == null) {
            treatmentDB = new TreatmentDB();
        }
        treatmentDB.setPartnerDB(Long.parseLong(line[1]));
        treatmentDB.setDiagnosis(Long.valueOf(line[2]));
        treatmentDB.setMessage(Long.valueOf(line[3]));
        treatmentDB.setType(Integer.parseInt(line[4]));
        return treatmentDB;
    }


}
