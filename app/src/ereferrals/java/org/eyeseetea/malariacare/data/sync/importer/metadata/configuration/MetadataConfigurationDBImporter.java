package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;


import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.database.converts.CountryVersionConverterFromDomainModelToDB;
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
import org.eyeseetea.malariacare.domain.boundary.converters.IConverter;
import org.eyeseetea.malariacare.domain.entity.Configuration;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MetadataConfigurationDBImporter {

    private IConverter<Question, QuestionDB> converter;

    private List<QuestionOptionDB> pendingOptionsWithRules = new ArrayList<>();

    private CountryVersionConverterFromDomainModelToDB converterCountry =
            new CountryVersionConverterFromDomainModelToDB();
    @NonNull
    private IMetadataConfigurationDataSource remoteDataSource;

    public MetadataConfigurationDBImporter(
            @NonNull IMetadataConfigurationDataSource remoteDataSource,
            @NonNull IConverter<Question, QuestionDB> converter) {
        this.remoteDataSource = remoteDataSource;
        this.converter = converter;
    }

    public void importMetadata(Program program) throws Exception {

        List<Configuration.CountryVersion> countryVersions =
                remoteDataSource.getCountriesVersions();

        for (Configuration.CountryVersion domainCountry : countryVersions) {
            try {
                if (domainCountry.getUid().equals(program.getId())) {
                    processCountryData(domainCountry);
                    break;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
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

        String countryCode = country.getCountry();
        int version = country.getVersion();

        if (isCountryNotAlreadyAdded(countryCode)) {
            updateMetadataFor(country);

        } else if (hasMetadataBeenUpdatedFor(countryCode, version)) {
            deletePreviousMetadata();
            updateMetadataFor(country);
        }
    }

    private boolean hasMetadataBeenUpdatedFor(String countryCode, int version) {
        return CountryVersionDB.isVersionGreater(countryCode, version);
    }

    private boolean isCountryNotAlreadyAdded(String countryCode) {
        return !CountryVersionDB.isCountryAlreadyAdded(countryCode);
    }

    private void updateMetadataFor(Configuration.CountryVersion country) throws Exception {
        List<Question> questions = remoteDataSource.getQuestionsFor(country.getReference());
        saveInDB(country);
        saveQuestionsInDB(questions, country);
    }

    private void saveInDB(Configuration.CountryVersion domainCountry) {
        CountryVersionDB dbCountryVersion = converterCountry.convert(domainCountry);
        dbCountryVersion.save();
        addProgramMetadata(domainCountry);
    }

    private void saveQuestionsInDB(List<Question> questions, Configuration.CountryVersion country) {

        for (Question question : questions) {
            QuestionDB questionDB = converter.convert(question);
            setQuestionRelations(questionDB, country);
            AnswerDB answerDB = newAnswerDBWith(questionDB);
            questionDB.setAnswer(answerDB);
            save(questionDB);
        }

        addingRulesToQuestion();
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
        if(phoneFormatDB !=null){
            phoneFormatDB.setId_program_fk(programDB.getId_program());
            phoneFormatDB.save();

        }
    }

    private void addingRulesToQuestion() {
        for (QuestionOptionDB questionOptionDBContainer : pendingOptionsWithRules) {

            OptionDB optionWithRule = questionOptionDBContainer.getOptionDB();
            List<String> matchQuestionsCode = optionWithRule.getMatchQuestionsCode();

            for (String matchQuestionCode : matchQuestionsCode) {

                QuestionDB questionMatch = QuestionDB.findByCode(matchQuestionCode);

                QuestionRelationDB questionRelationDB = saveQuestionRelationDB(questionMatch);

                MatchDB matchDB = saveMatchDB(questionRelationDB);

                saveQuestionOption(questionOptionDBContainer, optionWithRule, matchDB);
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

    private AnswerDB newAnswerDBWith(QuestionDB questionDB) {
        AnswerDB answerDB = new AnswerDB();
        answerDB.setName(questionDB.getCode());

        answerDB.save();
        return answerDB;
    }

    private void save(QuestionDB questionDB) {
        questionDB.save();

        save(questionDB.getOptionDBS(), questionDB);

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
