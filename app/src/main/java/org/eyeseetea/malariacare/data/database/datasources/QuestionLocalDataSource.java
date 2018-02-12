package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.domain.boundary.repositories.IQuestionRepository;
import org.eyeseetea.malariacare.domain.entity.Question;

import java.util.ArrayList;
import java.util.List;


public class QuestionLocalDataSource implements IQuestionRepository {
    @Override
    public List<Question> getQuestionsForProgram(String programUID) {
        List<QuestionDB> questionDBS = QuestionDB.listByProgram(
                ProgramDB.getProgram(programUID));

        List<Question> questions = new ArrayList<>();

        for (QuestionDB questionDB : questionDBS) {
            questions.add(
                    new Question(questionDB.getUid(), questionDB.getInternationalizedForm_name()));
        }
        return questions;
    }
}
