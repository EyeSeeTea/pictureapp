package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.database.model.OptionAttributeDB;
import org.eyeseetea.malariacare.data.database.model.OptionDB;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.data.mappers.QuestionMapper;
import org.eyeseetea.malariacare.domain.boundary.repositories.IQuestionRepository;
import org.eyeseetea.malariacare.domain.entity.Option;
import org.eyeseetea.malariacare.domain.entity.Question;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;


public class QuestionLocalDataSource implements IQuestionRepository {
    @Override
    public List<Question> getQuestionsByProgram(String programUID) {
        List<QuestionDB> questionDBS = QuestionDB.listByProgram(
                ProgramDB.getProgram(programUID));

        List<Question> questions = new ArrayList<>();

        for (QuestionDB questionDB : questionDBS) {
            questions.add(QuestionMapper.mapFromDbToDomain(questionDB));
        }
        return questions;
    }

    @Override
    public Question getByUId(String questionUId) {
        QuestionDB questionDB = QuestionDB.findByUID(questionUId);
        if(questionDB==null){
            return null;
        }
        return QuestionMapper.mapFromDbToDomain(questionDB);
    }

}
