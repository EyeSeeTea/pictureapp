package org.eyeseetea.malariacare.data.database.utils.populatedb;

import android.support.annotation.Nullable;

import org.eyeseetea.malariacare.data.database.model.Answer;
import org.eyeseetea.malariacare.data.database.model.Drug;
import org.eyeseetea.malariacare.data.database.model.DrugCombination;
import org.eyeseetea.malariacare.data.database.model.Header;
import org.eyeseetea.malariacare.data.database.model.Match;
import org.eyeseetea.malariacare.data.database.model.Option;
import org.eyeseetea.malariacare.data.database.model.OptionAttribute;
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

import java.util.HashMap;

public class PopulateRow {
    static Question populateQuestion(String[] line, HashMap<Long, Header> headerFK,
            HashMap<Long, Answer> answerFK, @Nullable Question question) {
        if (question == null) {
            question = new Question();
        }
        question.setCode(line[1]);
        question.setDe_name(line[2]);
        question.setHelp_text(line[3]);
        question.setForm_name(line[4]);
        question.setUid(line[5]);
        question.setOrder_pos(Integer.valueOf(line[6]));
        question.setNumerator_w(Float.valueOf(line[7]));
        question.setDenominator_w(Float.valueOf(line[8]));
        question.setHeader(headerFK.get(Long.valueOf(line[9])));
        if (!line[10].equals("")) {
            question.setAnswer(answerFK.get(Long.valueOf(line[10])));
        }
        question.setOutput(Integer.valueOf(line[12]));
        question.setTotalQuestions(Integer.valueOf(line[13]));
        question.setVisible(Integer.valueOf(line[14]));
        if (line.length > 15 && !line[15].equals("")) {
            question.setPath((line[15]));
        }
        if (line.length > 16 && !line[16].equals("")) {
            question.setCompulsory(Integer.valueOf(line[16]));
        } else {
            question.setCompulsory(Question.QUESTION_NOT_COMPULSORY);
        }
        return question;
    }

    static Answer populateAnswer(String line[], @Nullable Answer answer) {
        if (answer == null) {
            answer = new Answer();
        }
        answer.setName(line[1]);
        return answer;
    }

    static Header populateHeader(String line[], HashMap<Long, Tab> tabsFK,
            @Nullable Header header) {
        if (header == null) {
            header = new Header();
        }
        header.setShort_name(line[1]);
        header.setName(line[2]);
        header.setOrder_pos(Integer.valueOf(line[3]));
        header.setTab(tabsFK.get(Long.valueOf(line[4])));
        return header;
    }

    static Program populateProgram(String[] line, @Nullable Program program) {
        if (program == null) {
            program = new Program();
        }
        program.setUid(line[1]);
        program.setName(line[2]);
        return program;
    }

    static Tab populateTab(String[] line, HashMap<Long, Program> programFK, @Nullable Tab tab) {
        if (tab == null) {
            tab = new Tab();
        }
        tab.setName(line[1]);
        tab.setOrder_pos(Integer.valueOf(line[2]));
        tab.setProgram(programFK.get(Long.valueOf(line[3])));
        tab.setType(Integer.valueOf(line[4]));
        return tab;
    }

    static Match populateMatch(String line[],
            HashMap<Long, QuestionRelation> questionRelationFK, @Nullable Match match) {
        if (match == null) {
            match = new Match();
        }
        match.setQuestionRelation(questionRelationFK.get(Long.valueOf(line[1])));
        return match;
    }


    static QuestionThreshold populateQuestionThreshold(String[] line,
            HashMap<Long, Match> matchesFK, HashMap<Long, Question> quetiosnFK,
            @Nullable QuestionThreshold questionThreshold) {
        if (questionThreshold == null) {
            questionThreshold = new QuestionThreshold();
        }
        questionThreshold.setMatch(matchesFK.get(Long.valueOf(line[1])));
        questionThreshold.setQuestion(quetiosnFK.get(Long.valueOf(line[2])));
        if (!line[3].equals("")) {
            questionThreshold.setMinValue(Integer.valueOf(line[3]));
        }
        if (!line[4].equals("")) {
            questionThreshold.setMaxValue(Integer.valueOf(line[4]));
        }
        return questionThreshold;
    }

    static QuestionOption populateQuestionOption(String[] line, HashMap<Long, Question> questionFK,
            HashMap<Long, Option> optionFK, HashMap<Long, Match> matchFK,
            @Nullable QuestionOption questionOption) {
        if (questionOption == null) {
            questionOption = new QuestionOption();
        }
        questionOption.setQuestion(questionFK.get(Long.valueOf(line[1])));
        questionOption.setOption(optionFK.get(Long.valueOf(line[2])));
        if (!line[3].equals("")) {
            questionOption.setMatch(matchFK.get(Long.valueOf(line[3])));
        }
        return questionOption;
    }

    static QuestionRelation populateQuestionRelation(String[] line,
            HashMap<Long, Question> questionFK,
            @Nullable QuestionRelation questionRelation) {
        if (questionRelation == null) {
            questionRelation = new QuestionRelation();
        }
        questionRelation.setOperation(Integer.valueOf(line[1]));
        questionRelation.setQuestion(questionFK.get(Long.valueOf(line[2])));
        return questionRelation;
    }

    /**
     * Method to populate each row of TreatmentMatches.csv, execute after populateTreatments and
     * populateMatches.
     *
     * @param line The row of the csv to populate.
     */
    static TreatmentMatch populateTreatmentMatches(String[] line,
            HashMap<Long, Treatment> treatmentIds,
            HashMap<Long, Match> matchesIds, TreatmentMatch treatmentMatch) {
        if (treatmentMatch == null) {
            treatmentMatch = new TreatmentMatch();
        }
        treatmentMatch.setTreatment(treatmentIds.get(Long.parseLong(line[1])));
        treatmentMatch.setMatch(matchesIds.get(Long.parseLong(line[2])));
        return treatmentMatch;
    }

    /**
     * Method to populate each row of DrugCombinations.csv, execute after populateDrugs and
     * populateTreatments.
     *
     * @param line The row of the csv to populate.
     */
    static DrugCombination populateDrugCombinations(String[] line, HashMap<Long, Drug> drugsFK,
            HashMap<Long, Treatment> treatmentFK, @Nullable DrugCombination drugCombination) {
        if (drugCombination == null) {
            drugCombination = new DrugCombination();
        }
        drugCombination.setDrug(drugsFK.get(Long.parseLong(line[1])));
        drugCombination.setTreatment(treatmentFK.get(Long.parseLong(line[2])));
        drugCombination.setDose(Float.parseFloat(line[3]));
        return drugCombination;
    }

    /**
     * Method to populate each row of Treatment.csv, execute after populateOrganisations.
     *
     * @param line The row of the csv to populate.
     * @param stringKeyList
     */
    static Treatment populateTreatments(String[] line, HashMap<Long, Partner> organisationFK,
            HashMap<Long, StringKey> stringKeyList, @Nullable Treatment treatment) {
        if (treatment == null) {
            treatment = new Treatment();
        }
        treatment.setOrganisation(organisationFK.get(Long.parseLong(line[1])));
        treatment.setDiagnosis(stringKeyList.get(Long.valueOf(line[2])).getId_string_key());
        treatment.setMessage(stringKeyList.get(Long.valueOf(line[3])).getId_string_key());
        treatment.setType(Integer.parseInt(line[4]));
        return treatment;
    }

    /**
     * Method to populate each row of Partner.csv.
     *
     * @param line The row of the csv to populate.
     */
    static Partner populateOrganisations(String[] line, @Nullable Partner partner) {
        if (partner == null) {
            partner = new Partner();
        }
        partner.setUid(line[1]);
        partner.setName(line[2]);
        return partner;
    }

    /**
     * Method to populate the Drugs.csv.
     *
     * @param line The row of the csv to add to db.
     */
    static Drug populateDrugs(String line[], @Nullable Drug drug) {
        if (drug == null) {
            drug = new Drug();
        }
        drug.setName(line[1]);
        drug.setQuestion_code(line[2]);
        return drug;
    }

    public static Option populateOption(String[] line, HashMap<Long, Answer> answerFK,
            HashMap<Long, OptionAttribute> optionAttributeFK, @Nullable Option option) {
        if (option == null) {
            option = new Option();
        }
        option.setName(line[1]);
        option.setCode(line[2]);
        option.setFactor(Float.valueOf(line[3]));
        option.setAnswer(answerFK.get(Long.valueOf(line[4])));
        if (line[5] != null && !line[5].isEmpty()) {
            option.setOptionAttribute(
                    optionAttributeFK.get(Long.valueOf(line[5])));
        }
        return option;
    }

    static StringKey populateStringKey(String[] line, @Nullable StringKey stringKey) {
        if (stringKey == null) {
            stringKey = new StringKey();
        }
        stringKey.setKey(line[1]);
        return stringKey;
    }

    public static Translation populateTranslation(String[] line,
            HashMap<Long, StringKey> stringKeyFK,
            Translation translation) {
        if (translation == null) {
            translation = new Translation();
        }
        translation.setId_string_key(stringKeyFK.get(Long.valueOf(line[1])).getId_string_key());
        translation.setTranslation(line[2]);
        translation.setLanguage(line[3]);
        return translation;
    }
}
