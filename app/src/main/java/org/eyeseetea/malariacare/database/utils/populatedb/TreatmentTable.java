package org.eyeseetea.malariacare.database.utils.populatedb;

import android.content.Context;

import com.opencsv.CSVReader;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.database.model.Drug;
import org.eyeseetea.malariacare.database.model.DrugCombination;
import org.eyeseetea.malariacare.database.model.Match;
import org.eyeseetea.malariacare.database.model.QuestionOption;
import org.eyeseetea.malariacare.database.model.QuestionRelation;
import org.eyeseetea.malariacare.database.model.QuestionThreshold;
import org.eyeseetea.malariacare.database.model.Treatment;
import org.eyeseetea.malariacare.database.model.TreatmentMatch;
import org.eyeseetea.malariacare.database.utils.PreferencesState;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreatmentTable {
    private static final List<String> csvsToDelete = Arrays.asList(
            PopulateDB.MATCHES,
            PopulateDB.QUESTION_OPTIONS_CSV,
            PopulateDB.QUESTION_THRESHOLDS_CSV,
            PopulateDB.DRUGS_CSV,
            PopulateDB.TREATMENT_CSV,
            PopulateDB.DRUG_COMBINATIONS_CSV,
            PopulateDB.TREATMENT_MATCHES_CSV,
            PopulateDB.TREATMENT_TABLE_CSV);

    private Context mContext;
    private FileCsvs mFileCsvs;
    private String treatmentQuestionId;
    private String treatmentQuestionRelationId;
    private String ageQuestionId;
    private String pregnantQuestionId;
    private String severeQuestionId;
    private String rdtQuestionId;
    private String optionPregnantId;
    private String optionMaleId;
    private String optionFemaleId;
    private String optionSeverId;
    private String optionNoSeverId;
    private String optionRDTMixedId;
    private String optionRDTPfId;
    private String optionRDTPvId;


    public TreatmentTable() throws IOException {
        mContext = PreferencesState.getInstance().getContext();
        mFileCsvs = new FileCsvs();
        initTreatmentTable();
    }

    private void initTreatmentTable() throws IOException {
        deleteTreatmentTableFromCSV();
        deleteOldTreatmentTable();
        splitTreatmentTableToCsvs();
    }


    private void deleteTreatmentTableFromCSV() throws IOException {
        for (String csvName : csvsToDelete) {
            mFileCsvs.saveCsvFromAssetsToFile(csvName);
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

    private void splitTreatmentTableToCsvs() throws IOException {
        CSVReader reader = new CSVReader(
                new InputStreamReader(mContext.openFileInput(PopulateDB.TREATMENT_TABLE_CSV)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);

        initCSVQuestionOptionsIds();

        List<String[]> treatmentLines = new ArrayList<>();
        List<String[]> treatmentMatchLines = new ArrayList<>();
        List<String[]> drugsCombinationLines = new ArrayList<>();
        List<String[]> matchLines = getNotTreatmentLines(PopulateDB.MATCHES);
        List<String[]> questionThresholdLines = getNotTreatmentLines(
                PopulateDB.QUESTION_THRESHOLDS_CSV);
        List<String[]> questionOptionLines = getNotTreatmentLines(PopulateDB.QUESTION_OPTIONS_CSV);
        String[] line;
        while ((line = reader.readNext()) != null) {

            addTreatment(line, treatmentLines, treatmentMatchLines);
            addDrugCombinations(line, drugsCombinationLines, treatmentLines);

            if (line[5].equals("Y")) {
                addMatch(matchLines);
                addTreatmentMatch(treatmentMatchLines, matchLines, treatmentLines);
                addQuestionOptionAge(line, questionThresholdLines, matchLines);
                addQuestionOptionPregnant(line, questionOptionLines, matchLines);
                addQuestionOptionSevere(line, questionOptionLines, matchLines);
                addQuestionOptionRDT(line, questionOptionLines, matchLines);
            }

        }
    }


    private void initCSVQuestionOptionsIds() throws IOException {
        treatmentQuestionId = getCsvId(
                mContext.getResources().getString(R.string.dynamicTreatmentQuestionUID), 5,
                PopulateDB.QUESTIONS_CSV);
        treatmentQuestionRelationId = getCsvId(treatmentQuestionId, 2,
                QuestionRelation.TREATMENT_MATCH + "", 1, PopulateDB.QUESTION_RELATIONS_CSV);
        ageQuestionId = getCsvId(mContext.getResources().getString(R.string.ageQuestionUID),
                5, PopulateDB.QUESTIONS_CSV);
        pregnantQuestionId = getCsvId(
                mContext.getResources().getString(R.string.sexPregnantQuestionUID), 5,
                PopulateDB.QUESTIONS_CSV);
        severeQuestionId = getCsvId(
                mContext.getResources().getString(R.string.severeSymtomsQuestionUID), 5,
                PopulateDB.QUESTIONS_CSV);
        rdtQuestionId = getCsvId(mContext.getResources().getString(R.string.rdtQuestionUID),
                5, PopulateDB.QUESTIONS_CSV);
        optionPregnantId = getCsvId("patient_sex_female_pregnant",
                1, PopulateDB.OPTIONS_CSV);
        optionMaleId = getCsvId("patient_sex_male",
                1, PopulateDB.OPTIONS_CSV);
        optionFemaleId = getCsvId("patient_sex_female",
                1, PopulateDB.OPTIONS_CSV);
        optionSeverId = getCsvId("symptoms_severity_yes",
                1, PopulateDB.OPTIONS_CSV);
        optionNoSeverId = getCsvId("symptoms_severity_no",
                1, PopulateDB.OPTIONS_CSV);
        optionRDTMixedId = getCsvId("rdt_mixed",
                1, PopulateDB.OPTIONS_CSV);
        optionRDTPfId = getCsvId("rdt_pf",
                1, PopulateDB.OPTIONS_CSV);
        optionRDTPvId = getCsvId("rdt_pv",
                1, PopulateDB.OPTIONS_CSV);
    }


    private void addTreatment(String line[], List<String[]> treatmentLines,
            List<String[]> treatmentMatchLines) throws IOException {
        String[] treatmentLine = {getNextIdToInsert(treatmentLines), "1", line[6], line[9],
                (line[5].equals("Y")
                        ? Treatment.TYPE_MAIN : Treatment.TYPE_NOT_MAIN) + ""};
        mFileCsvs.insertCsvLine(PopulateDB.TREATMENT_CSV, treatmentLine);
        treatmentLines.add(treatmentLine);
        if (line[5].equals("N")) {
            String[] treatmentMatch =
                    {getNextIdToInsert(treatmentMatchLines), getPreviousMainTreatmentId(
                            treatmentLines), treatmentLine[0]};
            mFileCsvs.insertCsvLine(PopulateDB.TREATMENT_MATCHES_CSV, treatmentMatch);
            treatmentMatchLines.add(treatmentMatch);
        }
    }

    private void addDrugCombinations(String line[], List<String[]> drugsCombinationLines,
            List<String[]> treatmentLines) throws IOException {
        if (!line[12].isEmpty()) {
            addDrugTreatment(drugsCombinationLines, treatmentLines,
                    mContext.getResources().getString(R.string.act6QuestionUID), line[12]);
        }
        if (!line[13].isEmpty()) {
            addDrugTreatment(drugsCombinationLines, treatmentLines,
                    mContext.getResources().getString(R.string.act12QuestionUID), line[13]);
        }
        if (!line[14].isEmpty()) {
            addDrugTreatment(drugsCombinationLines, treatmentLines,
                    mContext.getResources().getString(R.string.act18QuestionUID), line[14]);
        }
        if (!line[15].isEmpty()) {
            addDrugTreatment(drugsCombinationLines, treatmentLines,
                    mContext.getResources().getString(R.string.act24QuestionUID), line[15]);
        }
        if (!line[16].isEmpty()) {
            addDrugTreatment(drugsCombinationLines, treatmentLines,
                    mContext.getResources().getString(R.string.cqQuestionUID), line[16]);
        }
        if (!line[17].isEmpty()) {
            addDrugTreatment(drugsCombinationLines, treatmentLines,
                    mContext.getResources().getString(R.string.pqQuestionUID), line[17]);
        }
    }

    private void addMatch(List<String[]> matchLines) throws IOException {
        String[] match = {getNextIdToInsert(matchLines), treatmentQuestionRelationId + ""};
        matchLines.add(match);
        mFileCsvs.insertCsvLine(PopulateDB.MATCHES, match);
    }

    private void addTreatmentMatch(List<String[]> treatmentMatchLines, List<String[]> matchLines,
            List<String[]> treatmentLines) throws IOException {
        String[] treatmentMatch = {getNextIdToInsert(treatmentMatchLines),
                getLastIdInserted(treatmentLines), getLastIdInserted(matchLines)};
        treatmentMatchLines.add(treatmentMatch);
        mFileCsvs.insertCsvLine(PopulateDB.TREATMENT_MATCHES_CSV, treatmentMatch);
    }

    private void addQuestionOptionAge(String[] line, List<String[]> questionThresholdLines,
            List<String[]> matchLines) throws IOException {
        String[] ages = line[1].split("-");
        String[] questionThreshold = {getNextIdToInsert(questionThresholdLines),
                getLastIdInserted(matchLines), ageQuestionId, ages[0],
                ages[1]};
        questionThresholdLines.add(questionThreshold);
        mFileCsvs.insertCsvLine(PopulateDB.QUESTION_THRESHOLDS_CSV, questionThreshold);
    }

    private void addQuestionOptionPregnant(String[] line, List<String[]> questionOptionLines,
            List<String[]> matchLines) throws IOException {
        if (line[2].equals("Y")) {
            String[] questionOptionPregnant = {getNextIdToInsert(questionOptionLines),
                    pregnantQuestionId, optionPregnantId, getLastIdInserted(
                    matchLines)};
            questionOptionLines.add(questionOptionPregnant);
            mFileCsvs.insertCsvLine(PopulateDB.QUESTION_OPTIONS_CSV,
                    questionOptionPregnant);
        } else {
            String[] questionOptionPregnant = {getNextIdToInsert(questionOptionLines),
                    pregnantQuestionId, optionFemaleId, getLastIdInserted(
                    matchLines)};
            questionOptionLines.add(questionOptionPregnant);
            mFileCsvs.insertCsvLine(PopulateDB.QUESTION_OPTIONS_CSV,
                    questionOptionPregnant);
            String[] questionOptionPregnant1 = {getNextIdToInsert(questionOptionLines),
                    pregnantQuestionId, optionMaleId, getLastIdInserted(
                    matchLines)};
            questionOptionLines.add(questionOptionPregnant1);
            mFileCsvs.insertCsvLine(PopulateDB.QUESTION_OPTIONS_CSV,
                    questionOptionPregnant1);
        }
    }

    private void addQuestionOptionSevere(String[] line, List<String[]> questionOptionLines,
            List<String[]> matchLines) throws IOException {
        String optionSevere = line[3].equals("Y") ? optionSeverId : optionNoSeverId;
        String[] questionOptionSevere = {getNextIdToInsert(questionOptionLines),
                severeQuestionId, optionSevere, getLastIdInserted(
                matchLines)};
        questionOptionLines.add(questionOptionSevere);
        mFileCsvs.insertCsvLine(PopulateDB.QUESTION_OPTIONS_CSV, questionOptionSevere);
    }

    private void addQuestionOptionRDT(String[] line, List<String[]> questionOptionLines,
            List<String[]> matchLines) throws IOException {
        String optionRDT = optionRDTMixedId;
        if (line[4].equals("Pf")) {
            optionRDT = optionRDTPfId;
        } else if (line[4].equals("Pv")) {
            optionRDT = optionRDTPvId;
        }
        String[] questionOptionRdt = {getNextIdToInsert(questionOptionLines),
                rdtQuestionId, optionRDT, getLastIdInserted(
                matchLines)};
        questionOptionLines.add(questionOptionRdt);
        mFileCsvs.insertCsvLine(PopulateDB.QUESTION_OPTIONS_CSV, questionOptionRdt);
    }


    private void addDrugTreatment(List<String[]> drugCombinations, List<String[]> treatments,
            String uid, String dose)
            throws IOException {
        String[] drugCombination = {getNextIdToInsert(drugCombinations), getCsvId(uid, 2,
                PopulateDB.DRUGS_CSV),
                getLastIdInserted(treatments), dose.replace(",", ".")};
        mFileCsvs.insertCsvLine(PopulateDB.DRUG_COMBINATIONS_CSV, drugCombination);
        drugCombinations.add(drugCombination);
    }

    private List<String[]> getNotTreatmentLines(String filename) throws IOException {
        List<String[]> lines = new ArrayList<>();
        CSVReader reader = new CSVReader(
                new InputStreamReader(mContext.openFileInput(filename)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String[] line;
        while ((line = reader.readNext()) != null) {
            lines.add(line);
        }
        return lines;
    }

    private String getCsvId(String fieldValue, int fieldPos, String csvName) throws IOException {
        CSVReader reader = new CSVReader(
                new InputStreamReader(mContext.openFileInput(csvName)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String line[];
        while ((line = reader.readNext()) != null) {
            if (line[fieldPos].equals(fieldValue)) {
                return line[0];
            }
        }
        return "1";
    }

    private String getCsvId(String fieldValue1, int fieldPos1, String fieldValue2, int fieldPos2,
            String csvName) throws IOException {
        CSVReader reader = new CSVReader(
                new InputStreamReader(mContext.openFileInput(csvName)),
                PopulateDB.SEPARATOR, PopulateDB.QUOTECHAR);
        String line[];
        while ((line = reader.readNext()) != null) {
            if (line[fieldPos1].equals(fieldValue1) && line[fieldPos2].equals(fieldValue2)) {
                return line[0];
            }
        }
        return "1";
    }


    /**
     * Method to get the last Main Treatment inserted
     */
    private String getPreviousMainTreatmentId(List<String[]> lines) {
        if (!(lines == null || lines.isEmpty())) {
            for (int i = lines.size() - 1; i >= 0; i--) {
                if (lines.get(i)[4].equals(Treatment.TYPE_MAIN + "")) {
                    return lines.get(i)[0];
                }
            }
        }
        return "1";
    }

    private String getNextIdToInsert(List<String[]> lines) {
        return (lines == null || lines.isEmpty()) ? "1" : (Integer.parseInt(
                lines.get(lines.size() - 1)[0]) + 1) + "";
    }

    private String getLastIdInserted(List<String[]> lines) {
        return (lines == null || lines.isEmpty()) ? "1" : lines.get(lines.size() - 1)[0];
    }

}
