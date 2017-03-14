package org.eyeseetea.malariacare.strategies;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.eyeseetea.malariacare.BaseActivity;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.authentication.AuthenticationManager;
import org.eyeseetea.malariacare.domain.boundary.IAuthenticationManager;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;

public class PopulateDBStrategy {

    public static void init() {
        try {
            FileCsvs fileCsvs = new FileCsvs();
            fileCsvs.saveCsvsInFileIfNeeded();
            TreatmentTableOperations treatmentTable = new TreatmentTableOperations();
            treatmentTable.generateTreatmentMatrixIFNeeded();
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    public static void updateOptions(Context context) throws IOException {
        List<Option> optionToDelete = Question.getOptions(
                PreferencesState.getInstance().getContext().getString(
                        R.string.residenceVillageUID));
        for (Option option : optionToDelete) {
            if (!option.getCode().equals(PreferencesState.getInstance().getContext().getString(
                    R.string.patientResidenceVillageOtherCode))) {
                option.delete();
            }
        }
        FileCsvs fileCsvs = new FileCsvs();
        fileCsvs.saveCsvFromAssetsToFile(PopulateDB.OPTIONS_CSV);
        List<Option> options = Option.getAllOptions();
        HashMap<Long, Answer> answersIds = RelationsIdCsvDB.getAnswerFKRelationCsvDB(context);
        HashMap<Long, OptionAttribute> optionAttributeIds =
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

        List<OrgUnit> orgUnits = OrgUnit.getAllOrgUnit();
        for (OrgUnit orgUnit : orgUnits) {
            Option option = new Option();
            option.setCode(orgUnit.getName());
            option.setName(orgUnit.getUid());
            option.setFactor((float) 0);
            option.setId_option((long) 0);
            option.setAnswer(Question.getAnswer(
                    PreferencesState.getInstance().getContext().getString(
                            R.string.residenceVillageUID)));
            option.save();
        }
    }
}
