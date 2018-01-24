package org.eyeseetea.malariacare.data.sync.importer.metadata.configuration;


import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionOptionDB;
import org.eyeseetea.malariacare.data.remote.IMetadataConfigurationDataSource;
import org.eyeseetea.malariacare.data.sync.importer.IConvertDomainDBVisitor;
import org.eyeseetea.malariacare.domain.entity.Question;

import java.util.List;

public class MetadataConfigurationDBImporter {

    private IConvertDomainDBVisitor<Question, QuestionDB> converter;

    public MetadataConfigurationDBImporter(
            @NonNull IConvertDomainDBVisitor<Question, QuestionDB> converter) {
        this.converter = converter;

    }

    public void importMetadataConfigurations(IMetadataConfigurationDataSource remoteDataSource

    ) throws Exception {

        List<Question> questions = remoteDataSource.getQuestions();

        saveQuestionsInDB(questions);
    }

    private void saveQuestionsInDB(List<Question> questions) {

        for (Question question : questions) {

            QuestionDB questionDB = converter.visit(question);

            save(questionDB);
        }
    }

    private void save(QuestionDB questionDB) {
        questionDB.save();

        List<QuestionOptionDB> questionOptionDBS = questionDB.getQuestionOptionDBS();

        save(questionOptionDBS, questionDB.getId_question());

    }

    private void save(List<QuestionOptionDB> questionOptionDBS, long questionID) {
        for (QuestionOptionDB questionOption : questionOptionDBS) {
            OptionDB optionDB = questionOption.getOptionDB();
            optionDB.save();

            questionOption.setQuestionDB(questionID);
            questionOption.setOptionDB(optionDB.getId_option());
            questionOption.save();

        }
    }

}
