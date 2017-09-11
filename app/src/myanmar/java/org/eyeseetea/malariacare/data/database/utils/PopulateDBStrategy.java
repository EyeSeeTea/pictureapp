package org.eyeseetea.malariacare.data.database.utils;

import android.content.Context;

import com.opencsv.CSVReader;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.Answer;
import org.eyeseetea.malariacare.data.database.model.Drug;
import org.eyeseetea.malariacare.data.database.model.DrugCombination;
import org.eyeseetea.malariacare.data.database.model.Header;
import org.eyeseetea.malariacare.data.database.model.Match;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.OptionAttribute;
import org.eyeseetea.malariacare.data.database.model.OrgUnit;
import org.eyeseetea.malariacare.data.database.model.Partner;
import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.model.Question;
import org.eyeseetea.malariacare.data.database.model.QuestionOption;
import org.eyeseetea.malariacare.data.database.model.QuestionRelation;
import org.eyeseetea.malariacare.data.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.data.database.model.StringKey;
import org.eyeseetea.malariacare.data.database.model.Tab;
import org.eyeseetea.malariacare.data.database.model.Translation;
import org.eyeseetea.malariacare.data.database.model.Treatment;
import org.eyeseetea.malariacare.data.database.model.TreatmentMatch;
import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.data.database.utils.populatedb.FileCsvs;
import org.eyeseetea.malariacare.data.database.utils.populatedb.IPopulateDBStrategy;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateRow;
import org.eyeseetea.malariacare.data.database.utils.populatedb.RelationsIdCsvDB;
import org.eyeseetea.malariacare.data.database.utils.populatedb.TreatmentTableOperations;
import org.eyeseetea.malariacare.data.sync.importer.OrgUnitToOptionConverter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PopulateDBStrategy implements IPopulateDBStrategy {

    public static List<Class<? extends BaseModel>> allMandatoryTables = Arrays.asList(
            User.class,
            StringKey.class,
            Translation.class,
            Program.class,
            Tab.class,
            Header.class,
            Answer.class,
            OptionAttribute.class,
            Option.class,
            Question.class,
            QuestionRelation.class,
            Match.class,
            QuestionOption.class,
            QuestionThreshold.class,
            Drug.class,
            Partner.class,
            Treatment.class,
            DrugCombination.class,
            TreatmentMatch.class,
            OrgUnit.class
    );

    @Override
    public void init() throws IOException {
        FileCsvs fileCsvs = new FileCsvs();
        fileCsvs.saveCsvsInFileIfNeeded();
        TreatmentTableOperations treatmentTable = new TreatmentTableOperations();
        treatmentTable.generateTreatmentMatrixIFNeeded();
    }

    @Override
    public InputStream openFile(Context context, String table)
            throws IOException, FileNotFoundException {
        return context.openFileInput(table);
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

    @Override
    public void createDummyOrganisationInDB() {
        Partner testOrganisation = new Partner();
        testOrganisation.setName(PreferencesState.getInstance().getContext().getString(
                R.string.test_partner_name));
        testOrganisation.setUid(PreferencesState.getInstance().getContext().getString(
                R.string.test_partner_uid));
        testOrganisation.insert();
    }



    @Override
    public void createDummyOrgUnitsDataInDB(Context context) {
        List<OrgUnit> orgUnits = OrgUnit.getAllOrgUnit();

        if (orgUnits.size() == 0) {
            try {
                PopulateDB.populateDummyData(context);
                OrgUnitToOptionConverter.convert();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void logoutWipe() {
        PopulateDB.wipeDataBase();
    }

    public static List<Class<? extends BaseModel>> getAllMandatoryTables() {
        return allMandatoryTables;
    }
}
