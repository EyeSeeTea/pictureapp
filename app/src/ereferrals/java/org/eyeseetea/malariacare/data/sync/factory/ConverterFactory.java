package org.eyeseetea.malariacare.data.sync.factory;

import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.mappers.OptionConvertFromDomainVisitor;
import org.eyeseetea.malariacare.data.mappers.QuestionConvertFromDomainVisitor;
import org.eyeseetea.malariacare.data.sync.importer.IConvertDomainDBVisitor;
import org.eyeseetea.malariacare.domain.entity.Option;
import org.eyeseetea.malariacare.domain.entity.Question;

public class ConverterFactory {
    private static IConvertDomainDBVisitor<Question, QuestionDB> questionConvertDomainToDb;
    private static IConvertDomainDBVisitor<Option, OptionDB> optionConvertDomainToDb;

    @NonNull
    public static IConvertDomainDBVisitor<Question, QuestionDB> getQuestionConverter() {


        if (questionConvertDomainToDb == null) {
            questionConvertDomainToDb = new QuestionConvertFromDomainVisitor(
                    getOptionConverter());
        }

        return questionConvertDomainToDb;
    }

    @NonNull
    public static IConvertDomainDBVisitor<Option, OptionDB> getOptionConverter() {


        if (optionConvertDomainToDb == null) {
            optionConvertDomainToDb = new OptionConvertFromDomainVisitor();
        }
        return optionConvertDomainToDb;
    }
}
