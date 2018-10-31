package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.Option;

import java.util.List;

public interface IOptionRepository {
    Option recoveryOptionsByQuestionAndValue(String questionUId, String value);
    List<Option> getOptionsByQuestion(String questionUId);
}