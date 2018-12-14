package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;


import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.database.converts.CountryVersionConvertFromDomainVisitor;
import org.eyeseetea.malariacare.data.database.model.AnswerDB;
import org.eyeseetea.malariacare.data.database.model.CountryVersionDB;
import org.eyeseetea.malariacare.data.database.model.HeaderDB;
import org.eyeseetea.malariacare.data.database.model.MatchDB;
import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.PhoneFormatDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionRelationDB;
import org.eyeseetea.malariacare.data.database.model.QuestionThresholdDB;
import org.eyeseetea.malariacare.data.database.model.SurveyDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.data.remote.IMetadataConfigurationDataSource;
import org.eyeseetea.malariacare.data.sync.importer.IConvertDomainDBVisitor;
import org.eyeseetea.malariacare.domain.entity.Configuration;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.domain.exception.WarningException;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetadataConfigurationDBImporter {

    private IConvertDomainDBVisitor<Question, QuestionDB> converter;

    private List<QuestionOptionDB> pendingOptionsWithRules = new ArrayList<>();
    private List<QuestionThresholdDB> pendingThresholdWithRules = new ArrayList<>();
    private Map<String, QuestionDB> mapQuestionsDBByCode = new HashMap<>();

    private CountryVersionConvertFromDomainVisitor converterCountry =
            new CountryVersionConvertFromDomainVisitor();
    @NonNull
    private IMetadataConfigurationDataSource remoteDataSource;

    private boolean needToDownloadMetadata;
    private List<Configuration.CountryVersion> countryVersions;

    public MetadataConfigurationDBImporter(
            @NonNull IMetadataConfigurationDataSource remoteDataSource,
            @NonNull IConvertDomainDBVisitor<Question, QuestionDB> converter) throws Exception {
        this.remoteDataSource = remoteDataSource;
        this.converter = converter;
        countryVersions = new ArrayList<>();
        needToDownloadMetadata = false;
    }

    public void importMetadata(Program program) throws Exception {
        fetchContriesVersionsIfRequired();

        for (Configuration.CountryVersion domainCountry : countryVersions) {
            try {
                if (domainCountry.getUid().equals(program.getId())) {
                    processCountryData(domainCountry);
                    break;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                throw new WarningException(exception.getMessage());
            }
        }
    }

    public boolean hasToUpdateMetadata(Program program) throws Exception {

        fetchContriesVersionsIfRequired();

        for (Configuration.CountryVersion domainCountry : countryVersions) {
            try {
                if (domainCountry.getUid().equals(program.getId())) {
                    String countryCode = domainCountry.getUid();
                    int version = domainCountry.getVersion();

                    if (isCountryNotAlreadyAdded(countryCode)) {
                        needToDownloadMetadata = true;
                    } else if (hasMetadataBeenUpdatedFor(countryCode, version)) {
                        needToDownloadMetadata = true;
                    }
                    break;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return needToDownloadMetadata;
    }

    private void fetchContriesVersionsIfRequired() throws Exception {
        if(countryVersions == null || countryVersions.isEmpty()){
            countryVersions = remoteDataSource.getCountriesCodesAndVersions();
        }
    }


    private void deletePreviousMetadata() {
        deleteDB();
    }

    private void deleteDB() {
        CountryVersionDB.deleteAll();
        PhoneFormatDB.deleteAll();
        SurveyDB.deleteAll();
        ProgramDB.deleteAll();
        TabDB.deleteAll();
        HeaderDB.deleteAll();
        AnswerDB.deleteAll();
        OptionAttributeDB.deleteAll();
        OptionDB.deleteAll();
        QuestionDB.deleteAll();
        QuestionRelationDB.deleteAll();
        MatchDB.deleteAll();
        QuestionOptionDB.deleteAll();
        QuestionThresholdDB.deleteAll();
    }

    private void addProgramMetadata(Configuration.CountryVersion countryVersion) {
        ProgramDB newProgramDB = new ProgramDB();
        newProgramDB.setName(countryVersion.getCountry());
        newProgramDB.setUid(countryVersion.getUid());
        newProgramDB.save();

        addProgramRelations(newProgramDB, countryVersion.getName());
    }

    private void addProgramRelations(ProgramDB newProgramDB, String name) {

        TabDB newTabDB = addNewTab(newProgramDB, name);
        addHeader(newTabDB);
    }

    private TabDB addNewTab(ProgramDB newProgramDB, String name) {
        TabDB newTabDB = new TabDB();
        newTabDB.setProgram(newProgramDB);
        newTabDB.setName(name);
        newTabDB.setOrder_pos(1);
        newTabDB.setType(Constants.TAB_MULTI_QUESTION);
        newTabDB.save();

        return newTabDB;
    }

    private HeaderDB addHeader(TabDB tabDB) {
        HeaderDB newHeaderDB = new HeaderDB();
        newHeaderDB.setName(tabDB.getName());
        newHeaderDB.setShort_name(tabDB.getName());
        newHeaderDB.setOrder_pos(1);
        newHeaderDB.setTabDB(tabDB);
        newHeaderDB.save();

        return newHeaderDB;
    }


    private void processCountryData(Configuration.CountryVersion country) throws Exception {

        String countryUID = country.getUid();
        int version = country.getVersion();
        List<Question> questions = remoteDataSource.getQuestionsByCountryCode(country.getReference());

        if (isCountryNotAlreadyAdded(countryUID)) {
            updateMetadataFor(questions,country);

        } else if (hasMetadataBeenUpdatedFor(countryUID, version)) {
            deletePreviousMetadata();
            updateMetadataFor(questions,country);
        }
    }

    private boolean hasMetadataBeenUpdatedFor(String countryUID, int version) {
        return CountryVersionDB.isVersionGreater(countryUID, version);
    }

    private boolean isCountryNotAlreadyAdded(String countryCode) {
        return !CountryVersionDB.isCountryAlreadyAdded(countryCode);
    }

    private void updateMetadataFor(List<Question> questions ,Configuration.CountryVersion country) throws Exception {
        saveInDB(country);
        saveQuestionsInDB(questions, country);
    }

    private void saveInDB(Configuration.CountryVersion domainCountry) {
        CountryVersionDB dbCountryVersion = converterCountry.visit(domainCountry);
        dbCountryVersion.save();
        addProgramMetadata(domainCountry);
    }

    private void saveQuestionsInDB(List<Question> questions, Configuration.CountryVersion country) {

        for (Question question : questions) {
            QuestionDB questionDB = converter.visit(question);
            setQuestionRelations(questionDB, country);
            save(questionDB);

            mapQuestionsDBByCode.put(questionDB.getCode(), questionDB);

            if (question.getRules() != null) {

                for (Question.Rule rule : question.getRules()) {
                    for (Question.Rule.Condition condition : rule.getConditions()) {
                        addThreshold(questionDB, rule, condition, condition.getOperator());
                    }
                }
            }
        }

        addingRulesToQuestion();
    }

    private void addThreshold(QuestionDB questionDB, Question.Rule rule,
            Question.Rule.Condition condition,
            Question.Rule.Operator operator) {
        int value = Integer.parseInt(condition.getRight().getValue());
        QuestionThresholdDB questionThresholdDB = new QuestionThresholdDB();

        switch (operator) {
            case EQUAL: {
                questionThresholdDB.setMinValue(value);
                questionThresholdDB.setMaxValue(value);
                break;
            }
            case GREATER_THAN: {
                questionThresholdDB.setMinValue(value + 1);
                break;
            }
            case GREATER_OR_EQUAL_THAN: {
                questionThresholdDB.setMinValue(value);
                break;
            }
            case LESS_THAN: {
                questionThresholdDB.setMaxValue(value - 1);
                break;
            }
            case LESS_OR_EQUAL_THAN: {
                questionThresholdDB.setMaxValue(value);
                break;
            }
        }

        questionThresholdDB.setQuestionDB(questionDB);
        pendingThresholdWithRules.add(questionThresholdDB);

        List<String> matchQuestionsCode = new ArrayList<>();
        for (Question.Rule.Action action : rule.getActions()) {
            matchQuestionsCode.add(action.getTargetQuestion());
        }
        questionThresholdDB.setMatchQuestionsCode(matchQuestionsCode);
    }

    private void setQuestionRelations(QuestionDB questionDB, Configuration.CountryVersion country) {
        String uid = country.getUid();
        ProgramDB programDB = ProgramDB.findByUID(uid);

        Long programID = programDB.getId_program();
        TabDB tabDB = TabDB.getFirstTabWithProgram(programID);

        HeaderDB headerDB = HeaderDB.findFirstHeaderByTab(tabDB.getId_tab());
        Long headerID = headerDB.getId_header();

        questionDB.setHeaderDB(headerID);

        addPhoneFormatIfRequired(questionDB, programDB);

    }

    private void addPhoneFormatIfRequired(QuestionDB questionDB, ProgramDB programDB) {
        PhoneFormatDB phoneFormatDB = questionDB.getPhoneFormatDB();
        if (phoneFormatDB != null) {
            phoneFormatDB.setId_program_fk(programDB.getId_program());
            phoneFormatDB.save();

        }
    }

    private void addingRulesToQuestion() {
        for (QuestionOptionDB questionOptionDBContainer : pendingOptionsWithRules) {

            OptionDB optionWithRule = questionOptionDBContainer.getOptionDB();
            List<String> matchQuestionsCode = optionWithRule.getMatchQuestionsCode();

            for (String matchQuestionCode : matchQuestionsCode) {

                QuestionDB questionMatch = mapQuestionsDBByCode.get(matchQuestionCode);

                QuestionRelationDB questionRelationDB = saveQuestionRelationDB(questionMatch);

                MatchDB matchDB = saveMatchDB(questionRelationDB);

                saveQuestionOption(questionOptionDBContainer, optionWithRule, matchDB);
            }

        }

        for (QuestionThresholdDB questionThresholdDB : pendingThresholdWithRules) {
            List<String> matchQuestionsCode = questionThresholdDB.getMatchQuestionsCode();

            for (String matchQuestionCode : matchQuestionsCode) {

                QuestionDB questionMatch = mapQuestionsDBByCode.get(matchQuestionCode);

                QuestionRelationDB questionRelationDB = saveQuestionRelationDB(questionMatch);

                MatchDB matchDB = saveMatchDB(questionRelationDB);

                questionThresholdDB.setMatchDB(matchDB);
                questionThresholdDB.save();
            }
        }


    }

    private void saveQuestionOption(QuestionOptionDB questionOptionDBContainer,
            OptionDB optionWithRule, MatchDB matchDB) {
        QuestionOptionDB newQuestionOptionDB = new QuestionOptionDB();
        newQuestionOptionDB.setOption(optionWithRule);
        newQuestionOptionDB.setQuestion(questionOptionDBContainer.getQuestionDB());
        newQuestionOptionDB.setMatchDB(matchDB.getId_match());

        newQuestionOptionDB.save();
    }

    @NonNull
    private MatchDB saveMatchDB(QuestionRelationDB questionRelationDB) {
        MatchDB matchDB = new MatchDB();
        matchDB.setQuestionRelation(questionRelationDB.getId_question_relation());
        matchDB.save();
        return matchDB;
    }

    @NonNull
    private QuestionRelationDB saveQuestionRelationDB(@NonNull QuestionDB questionMatch) {
        QuestionRelationDB questionRelationDB = new QuestionRelationDB();
        questionRelationDB.setOperation(QuestionRelationDB.PARENT_CHILD);
        questionRelationDB.setQuestion(questionMatch.getId_question());
        questionRelationDB.save();
        return questionRelationDB;
    }

    private void save(QuestionDB questionDB) {
        AnswerDB answerDB = questionDB.getAnswerDB();
        List<OptionDB> questionOptionDBS = answerDB.getOptionDBs();
        answerDB.setName(questionDB.getCode());
        answerDB.save();
        questionDB.setAnswer(answerDB);
        questionDB.save();

        save(questionOptionDBS, questionDB);

    }

    private void save(List<OptionDB> questionOptionDBS, QuestionDB questionDB) {

        AnswerDB answerDB = questionDB.getAnswerDB();

        for (OptionDB optionDB : questionOptionDBS) {
            optionDB.setAnswerDB(answerDB);
            optionDB.save();

            if (optionDB.hasMatches()) {

                QuestionOptionDB questionOptionDB = new QuestionOptionDB();
                questionOptionDB.setOption(optionDB);
                questionOptionDB.setQuestion(questionDB);

                pendingOptionsWithRules.add(questionOptionDB);

            }
        }
    }

}
