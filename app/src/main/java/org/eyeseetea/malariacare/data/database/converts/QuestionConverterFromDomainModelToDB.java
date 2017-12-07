package org.eyeseetea.malariacare.data.database.converts;


import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.NotNull;

import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.domain.boundary.converters.IConverter;
import org.eyeseetea.malariacare.domain.entity.Option;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class QuestionConverterFromDomainModelToDB implements IConverter<Question, QuestionDB> {

    private IConverter<Option, OptionDB> optionConverter;

    public QuestionConverterFromDomainModelToDB(
            @NotNull IConverter<Option, OptionDB>
                    optionConverter) {
        this.optionConverter = optionConverter;
    }

    @NotNull
    @Override
    public QuestionDB convert(@NotNull Question domainModel) {
        QuestionDB dbModel = new QuestionDB();

        dbModel.setCode(domainModel.getCode());
        dbModel.setOrder_pos(domainModel.getIndex());
        dbModel.setDe_name(domainModel.getName());
        dbModel.setForm_name(domainModel.getName());
        dbModel.setOutput(getOutFrom(domainModel.getType()));
        dbModel.setCompulsory(getCompulsoryFrom(domainModel.isCompulsory()));
        dbModel.setOptionDBS(getOptionDBsFrom(domainModel));
        dbModel.setHeaderDB(domainModel.getHeader().getId());
        dbModel.setTotalQuestions(1);
        dbModel.setVisible(getVisibilityFrom(domainModel));

        return dbModel;
    }

    private int getVisibilityFrom(@NotNull Question question) {
        int visibilityIntValue = 0;

        switch (question.getVisibility()) {

            case VISIBLE:
                visibilityIntValue = QuestionDB.QUESTION_VISIBLE;
                break;
            case INVISIBLE:
                visibilityIntValue = QuestionDB.QUESTION_INVISIBLE;
                break;
            case IMPORTANT:
                visibilityIntValue = QuestionDB.QUESTION_IMPORTANT;
                break;
        }

        return visibilityIntValue;
    }

    private int getOutFrom(@NotNull Question.Type controlType) {
        int finalOutput = 0;

        switch (controlType) {

            case SHORT_TEXT:
                finalOutput = Constants.SHORT_TEXT;
                break;
            case PHONE:
                finalOutput = Constants.PHONE;
                break;
            case DROPDOWN_LIST:
                finalOutput = Constants.DROPDOWN_OU_LIST;
                break;
            case YEAR:
                finalOutput = Constants.YEAR;
                break;
            case DATE:
                finalOutput = Constants.DATE;
                break;
            case LONG_TEXT:
                finalOutput = Constants.LONG_TEXT;
                break;

            case POSITIVE_INT:
                finalOutput = Constants.POSITIVE_INT;
                break;

            case PREGNANT_MONTH:
                finalOutput = Constants.PREGNANT_MONTH_INT;
                break;

            case RADIO_GROUP_HORIZONTAL:
                finalOutput = Constants.RADIO_GROUP_HORIZONTAL;
                break;

            case QUESTION_LABEL:
                finalOutput = Constants.QUESTION_LABEL;
                break;

            case SWITCH_BUTTON:
                finalOutput = Constants.SWITCH_BUTTON;
                break;

        }

        return finalOutput;
    }

    private int getCompulsoryFrom(boolean mandatory) {
        return (mandatory) ? 1 : 0;
    }

    @NotNull
    private List<OptionDB> getOptionDBsFrom(@NotNull Question questionDomain) {
        List<OptionDB> optionDBS = new ArrayList<>();

        if (questionDomain.hasOptions()) {
            for (Option domainOption : questionDomain.getOptions()) {

                OptionDB newOptionDB = optionConverter.convert(domainOption);

                optionDBS.add(newOptionDB);


                if (domainOption.hasRules()) {

                    List<Option.Rule> domainRules = domainOption.getRules();
                    List<String> dbRules = convertTODBRulesFrom(domainRules);

                    newOptionDB.setMatchQuestionsCode(dbRules);

                }

            }
        }
        return optionDBS;
    }

    @NonNull
    private List<String> convertTODBRulesFrom(@NonNull List<Option.Rule> domainRules) {
        List<String> dbRules = new ArrayList<>();

        for (Option.Rule domainRule : domainRules) {
            dbRules.add(domainRule.getActionSubject().getCode());
        }
        return dbRules;
    }

}
