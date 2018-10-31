package org.eyeseetea.malariacare.data.database.datasources;

import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.data.database.model.AnswerDB;
import org.eyeseetea.malariacare.data.database.model.AnswerDB_Table;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB_Table;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB_Table;
import org.eyeseetea.malariacare.domain.boundary.repositories.IOptionRepository;
import org.eyeseetea.malariacare.domain.entity.Option;

import java.util.ArrayList;
import java.util.List;

import static org.eyeseetea.malariacare.data.database.AppDatabase.answerAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.answerName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.optionAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.optionName;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionAlias;
import static org.eyeseetea.malariacare.data.database.AppDatabase.questionName;

public class OptionLocalDataSource implements IOptionRepository {
    @Override
    public Option recoveryOptionsByQuestionAndValue(String questionUId, String value) {
        OptionDB optionDB = OptionDB.getOptionsByQuestionAndValue(questionUId, value);
        if(optionDB == null){
            return null;
        }
        optionDB = OptionDB.getById(optionDB.getId_option());//fix Option fields (dbflow bug)
        return new Option(optionDB.getId_option(), optionDB.getCode(), optionDB.getName());
    }

    @Override
    public List<Option> getOptionsByQuestion(String questionUId) {
        List<OptionDB> optionDBs = getOptionsDBByQuestion(questionUId);
        if(optionDBs == null){
            return null;
        }
        List<Option> options = new ArrayList<>();
        for(OptionDB optionDB : optionDBs ){
            optionDB = OptionDB.getById(optionDB.getId_option());//fix Option fields (dbflow bug)
            options.add(new Option(optionDB.getId_option(), optionDB.getCode(), optionDB.getName()));
        }
        return options;
    }


    private List<OptionDB> getOptionsDBByQuestion(String questionUid) {
        return new Select().from(OptionDB.class).as(optionName)
                .join(AnswerDB.class, Join.JoinType.LEFT_OUTER).as(answerName)
                .on(OptionDB_Table.id_answer_fk.withTable(optionAlias)
                        .eq(AnswerDB_Table.id_answer.withTable(answerAlias)))
                .join(QuestionDB.class, Join.JoinType.LEFT_OUTER).as(questionName)
                .on(QuestionDB_Table.id_answer_fk.withTable(questionAlias)
                        .eq(AnswerDB_Table.id_answer.withTable(answerAlias)))
                .where(QuestionDB_Table.uid_question.withTable(questionAlias).eq(questionUid))
                .orderBy(OptionDB_Table.name.withTable(optionAlias), true)
                .queryList();
    }
}
