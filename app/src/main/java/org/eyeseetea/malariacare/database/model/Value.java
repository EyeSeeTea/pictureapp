package org.eyeseetea.malariacare.database.model;

import com.orm.SugarRecord;
import com.orm.query.Select;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.eyeseetea.malariacare.database.AppDatabase;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

@Table(databaseName = AppDatabase.NAME)
public class Value extends SugarRecord<Value> {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_value;

    @Column
    String value;

    @ForeignKey(references = {@ForeignKeyReference(columnName = "id_question",
            columnType = Long.class,
            foreignColumnName = "id_question")},
            saveForeignKeyModel = false)
    Question question;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "id_survey",
            columnType = Long.class,
            foreignColumnName = "id_survey")},
            saveForeignKeyModel = false)
    Survey survey;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "id_option",
            columnType = Long.class,
            foreignColumnName = "id_option")},
            saveForeignKeyModel = false)
    Option option;

    public Value() {
    }

    public Value(String value, Question question, Survey survey) {
        this.option = null;
        this.question = question;
        this.value = value;
        this.survey = survey;
    }

    public Value(Option option, Question question, Survey survey) {
        this.option = option;
        this.question = question;
        this.value = option.getName();
        this.survey = survey;
    }

    public Long getId_value() {
        return id_value;
    }

    public void setId_value(Long id_value) {
        this.id_value = id_value;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }


    /**
     * The value is 'Positive' from a dropdown
     * @return true|false
     */
    public boolean isAPositive() {
        return getOption() != null && getOption().getName().equals("Positive");
    }

    /**
     * The value is 'Negative' from a dropdown
     * @return true|false
     */
    public boolean isANegative() {
        return getOption() != null && getOption().getName().equals("Negative");
    }

    /**
     * The value is 'isANotTested' from a dropdown
     * @return true|false
     */
    public boolean isANotTested() {
        return getOption() != null && getOption().getName().equals("Not Tested");
    }

    public static int countBySurvey(Survey survey){
        return 0;
        //TODO
//        if(survey==null || survey.getId_survey()==null){
//            return 0;
//        }
//        return (int) new Select().count()
//                .from(Value.class)
//                .where(Condition.column(Value$Table.SURVEY_ID_SURVEY).eq(survey.getId_survey())).count();
    }

    public static List<Value> listAllBySurvey(Survey survey){
        return null;
        //TODO
//        if(survey==null || survey.getId_survey()==null){
//            return new ArrayList<Value>();
//        }
//        return Select.from(Value.class)
//                .where(com.orm.query.Condition.prop("survey").eq(survey.getId_survey()))
//                .orderBy("id")
//                .list();
    }

    @Override
    public String toString() {
        return "Value{" +
                "option=" + option +
                ", question=" + question +
                ", value='" + value + '\'' +
                ", survey=" + survey +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Value)) return false;

        Value value1 = (Value) o;

        if (!option.equals(value1.option)) return false;
        if (!question.equals(value1.question)) return false;
        if (!survey.equals(value1.survey)) return false;
        if (!value.equals(value1.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = option.hashCode();
        result = 31 * result + question.hashCode();
        result = 31 * result + value.hashCode();
        result = 31 * result + survey.hashCode();
        return result;
    }

}
