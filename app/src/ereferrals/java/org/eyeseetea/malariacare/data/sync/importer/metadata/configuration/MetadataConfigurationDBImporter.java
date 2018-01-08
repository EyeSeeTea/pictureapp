package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;


import org.eyeseetea.malariacare.data.database.model.CountryVersionDB;

import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.database.converts.CountryVersionConverterFromDomainModelToDB;
import org.eyeseetea.malariacare.data.database.model.AnswerDB;
import org.eyeseetea.malariacare.data.database.model.HeaderDB;
import org.eyeseetea.malariacare.data.database.model.MatchDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.PhoneFormatDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionRelationDB;
import org.eyeseetea.malariacare.data.database.model.TabDB;
import org.eyeseetea.malariacare.domain.boundary.converters.IConverter;
import org.eyeseetea.malariacare.domain.entity.Configuration;
import org.eyeseetea.malariacare.domain.entity.Question;

import java.util.ArrayList;
import java.util.Date;
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

    public void importMetadata() throws Exception {

        List<Configuration.CountryVersion> countryVersions =
                remoteDataSource.getCountriesVersions();

        for (Configuration.CountryVersion domainCountry : countryVersions) {
            processCountryData(domainCountry);
        }

    }

    private void deleteCountryPreviousMetadata(Configuration.CountryVersion countryVersion) {

        deleteProgramMetadata(countryVersion);

    }

    private void deleteProgramMetadata(Configuration.CountryVersion countryVersion) {
        String uid = countryVersion.getUid();

        deleteProgramRelations(uid);

        ProgramDB.deleteProgramsBy(uid);
    }

    private void deleteProgramRelations(String uid) {

        ProgramDB programDB = ProgramDB.findBy(uid);

        Long programID = programDB.getId_program();

        PhoneFormatDB.deleteProgramsBy(programID);

        deleteTabMetadataBy(programID);

    }

    private void deleteTabMetadataBy(long programID) {
        List<TabDB> tabDBS = TabDB.findTabByProgram(programID);
        for (TabDB tabDB : tabDBS) {

            long tabID = tabDB.getId_tab();

            List<HeaderDB> headerDBList = HeaderDB.findHeadersByTab(tabID);

            deleteHeaderMetadata(headerDBList);

            tabDB.delete();
        }
    }

    private void deleteHeaderMetadata(List<HeaderDB> headerDBS) {
        for (HeaderDB headerDB : headerDBS) {
            long headerID = headerDB.getId_header();

            deleteQuestionMetadata(headerID);

            headerDB.delete();

        }
    }

    private void deleteQuestionMetadata(long headerID) {
        List<QuestionDB> questionDBS = QuestionDB.findQuestionsByHeader(headerID);

        for (QuestionDB questionDB : questionDBS) {

            deleteQuestionRelations(questionDB);
            questionDB.delete();
        }
    }

    private void deleteQuestionRelations(QuestionDB questionDB) {
        long questionID = questionDB.getId_question();
        long answerID = questionDB.getId_answer_fk();

        AnswerDB.deleteBy(answerID);
        OptionDB.deleteByAnswer(answerID);
        QuestionOptionDB.deleteQuestionOptionsBy(questionID);
        MatchDB.deleteMatchBy(questionID);
        QuestionRelationDB.deleteQuestionRelationBy(questionID);

    }


    private void processCountryData(Configuration.CountryVersion country) throws Exception {

        String countryCode = country.getCountry();
        int version = country.getVersion();

        if (isCountryNotAlreadyAdded(countryCode)) {
            saveInDB(country);
            updateMetadataFor(country);

        } else if (hasMetadataBeenUpdatedFor(countryCode, version)) {
            deleteCountryPreviousMetadata(country);
            updateMetadataFor(country);
            updateInDB(country);
        }
    }

    private boolean hasMetadataBeenUpdatedFor(String countryCode, int version) {
        return CountryVersionDB.isVersionGreater(countryCode, version);
    }

    private boolean isCountryNotAlreadyAdded(String countryCode) {
        return !CountryVersionDB.isCountryAlreadyAdded(countryCode);
    }

    private void updateMetadataFor(Configuration.CountryVersion country) throws Exception {
        List<Question> questions = remoteDataSource.getQuestionsFor(country.getCountry());

        saveQuestionsInDB(questions, country);
    }

    private void saveInDB(Configuration.CountryVersion domainCountry) {
        CountryVersionDB dbCountryVersion = converterCountry.convert(domainCountry);
        dbCountryVersion.save();
    }

    private void updateInDB(Configuration.CountryVersion domainCountry) {
        CountryVersionDB dbCountryVersion = converterCountry.convert(domainCountry);
        dbCountryVersion.setLast_update(new Date());
        dbCountryVersion.update();
    }


    private void saveQuestionsInDB(List<Question> questions, Configuration.CountryVersion country) {

        for (Question question : questions) {

            QuestionDB questionDB = converter.convert(question);
            setDefaultHeader(questionDB, country);
            AnswerDB answerDB = newAnswerDBWith(questionDB);
            questionDB.setAnswer(answerDB);
            save(questionDB);
        }

        addingRulesToQuestion();
    }

    private void setDefaultHeader(QuestionDB questionDB, Configuration.CountryVersion country) {
        String uid = country.getUid();
        ProgramDB programDB = ProgramDB.findByUID(uid);

        Long programID = programDB.getId_program();
        TabDB tabDB = TabDB.getFirstTabWithProgram(programID);

        HeaderDB headerDB = HeaderDB.findFirstHeaderByTab(tabDB.getId_tab());
        Long headerID = headerDB.getId_header();

        questionDB.setHeaderDB(headerID);

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
