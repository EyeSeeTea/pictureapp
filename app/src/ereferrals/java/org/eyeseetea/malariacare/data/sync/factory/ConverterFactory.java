package org.eyeseetea.malariacare.data.sync.factory;

import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.mappers.OptionConvertDomainDBVisitorFromDomainModelToDB;
import org.eyeseetea.malariacare.data.mappers.QuestionConvertDomainDBVisitorFromDomainModelToDB;
import org.eyeseetea.malariacare.data.sync.importer.IConvertDomainDBVisitor;
import org.eyeseetea.malariacare.domain.entity.Option;
import org.eyeseetea.malariacare.domain.entity.Question;

public class ConverterFactory {
    private static IConvertDomainDBVisitor<Question, QuestionDB> questionConverterDomainToDb;
    private static IConvertDomainDBVisitor<Option, OptionDB> optionConverterDomainToDb;

    @NonNull
    public static IConvertDomainDBVisitor<Question, QuestionDB> getQuestionConverter() {


        if (questionConverterDomainToDb == null) {
            questionConverterDomainToDb = new QuestionConvertDomainDBVisitorFromDomainModelToDB(
                    getOptionConverter());
        }

        return questionConverterDomainToDb;
    }

    @NonNull
    public static IConvertDomainDBVisitor<Option, OptionDB> getOptionConverter() {


        if (optionConverterDomainToDb == null) {
            optionConverterDomainToDb = new OptionConvertDomainDBVisitorFromDomainModelToDB();
        }
        return optionConverterDomainToDb;
    }
}
}
