package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.Question;

import java.util.List;

public interface IQuestionRepository {
    List<Question> getQuestionsByProgram(String programUID);
    boolean hasOptions(String questionUId);
    boolean existsByUId(String questionUId);
}
