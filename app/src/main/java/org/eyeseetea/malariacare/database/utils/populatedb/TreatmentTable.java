package org.eyeseetea.malariacare.database.utils.populatedb;

import android.content.Context;

import org.eyeseetea.malariacare.database.model.Drug;
import org.eyeseetea.malariacare.database.model.DrugCombination;
import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.QuestionOption;
import org.eyeseetea.malariacare.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.database.model.Treatment;
import org.eyeseetea.malariacare.database.model.TreatmentMatch;
import org.eyeseetea.malariacare.database.utils.PreferencesState;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TreatmentTable {
    public static final String TREATMENT_TABLE_CSV = "TreatmentTable.csv";
    private static final List<String> csvsToDelete = Arrays.asList(
            PopulateDB.MATCHES,
            PopulateDB.QUESTION_OPTIONS_CSV,
            PopulateDB.QUESTION_THRESHOLDS_CSV,
            PopulateDB.DRUGS_CSV,
            PopulateDB.TREATMENT_CSV,
            PopulateDB.DRUG_COMBINATIONS_CSV,
            PopulateDB.TREATMENT_MATCHES_CSV);

    private Context mContext;
    private Long questionRelationCSVId;

    public TreatmentTable() throws IOException {
        mContext = PreferencesState.getInstance().getContext();
        initTreatmentTable();
    }

    private void initTreatmentTable() throws IOException {
        deleteTreamentTableFromCSV();
        deleteOldTreatmentTable();
    }

    private void deleteTreamentTableFromCSV() throws IOException {
        FileCsvs fileCsvs = new FileCsvs();
        for (String csvName : csvsToDelete) {
            fileCsvs.saveCsvFromAssetsToFile(csvName);
        }
    }


    private void deleteOldTreatmentTable() throws IOException {
        List<TreatmentMatch> treatmentMatches = TreatmentMatch.getAllTreatmentMatches();
        for (TreatmentMatch treatmentMatch : treatmentMatches) {
            deleteRelatedTablesLines(treatmentMatch);
            treatmentMatch.delete();
        }
        List<Treatment> treatments = Treatment.getAllTreatments();
        for (Treatment treatment : treatments) {
            treatment.delete();
        }
        List<DrugCombination> drugCombinations = DrugCombination.getAllDrugCombination();
        for (DrugCombination drugCombination : drugCombinations) {
            drugCombination.delete();
        }
        List<Drug> drugs = Drug.getAllDrugs();
        for (Drug drug : drugs) {
            drug.delete();
        }
    }

    /**
     * Deleting the matches, questionOption and questionThresholds related with the treatment
     * table.
     */
    private void deleteRelatedTablesLines(TreatmentMatch treatmentMatch) throws IOException {
        Match match = treatmentMatch.getMatch();
        questionRelationCSVId = match.getQuestionRelation().getId_question_relation();

        List<QuestionOption> questionOptions = QuestionOption.getQuestionOptionsWithMatchId(
                match.getId_match());

        for (QuestionOption questionOption : questionOptions) {
            questionOption.delete();
        }
        List<QuestionThreshold> questionThresholds =
                QuestionThreshold.getQuestionThresholdsWithMatch(match.getId_match());
        for (QuestionThreshold questionThreshold : questionThresholds) {
            questionThreshold.delete();
        }
        match.delete();
    }

}
