package org.eyeseetea.malariacare.data.database.migrations;

import android.content.Context;

import com.opencsv.CSVReader;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.AnswerDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.OrgUnitDB;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.database.utils.populatedb.FileCsvs;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateRow;
import org.eyeseetea.malariacare.data.database.utils.populatedb.RelationsIdCsvDB;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

public class UpdateDBStrategy {
    public static void updateOptions(Context context) throws IOException {
        List<OptionDB> optionToDelete = Question.getOptions(
                PreferencesState.getInstance().getContext().getString(
                        R.string.residenceVillageUID));
        for (OptionDB option : optionToDelete) {
            if (!option.getName().equals(PreferencesState.getInstance().getContext().getString(
                    R.string.patientResidenceVillageOtherCode))) {
                option.delete();
            }
        }
        FileCsvs fileCsvs = new FileCsvs();
        fileCsvs.saveCsvFromAssetsToFile(PopulateDB.OPTIONS_CSV);
        List<OptionDB> options = OptionDB.getAllOptions();
        HashMap<Long, AnswerDB> answersIds = RelationsIdCsvDB.getAnswerFKRelationCsvDB(context);
        HashMap<Long, OptionAttributeDB> optionAttributeIds =
                RelationsIdCsvDB.getOptionAttributeIdRelationCsvDB(context);
        CSVReader reader = new CSVReader(
                new InputStreamReader(context.openFileInput(PopulateDB.OPTIONS_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String line[];
        int i = 0;
        while ((line = reader.readNext()) != null) {
            if (i < options.size()) {
                PopulateRow.populateOption(line, answersIds, optionAttributeIds,
                        options.get(i)).save();
            } else {
                PopulateRow.populateOption(line, answersIds, optionAttributeIds,
                        null).insert();
            }
            i++;
        }

        List<OrgUnitDB> orgUnits = OrgUnitDB.getAllOrgUnit();
        for (OrgUnitDB orgUnit : orgUnits) {
            OptionDB option = new OptionDB();
            option.setName(orgUnit.getName());
            option.setCode(orgUnit.getUid());
            option.setFactor((float) 0);
            option.setId_option((long) 0);
            option.setAnswerDB(Question.getAnswer(
                    PreferencesState.getInstance().getContext().getString(
                            R.string.residenceVillageUID)));
            option.save();
        }

    }
}
