package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.Option;

public interface IOptionRepository {
    Option recoveryOptionsByQuestionAndValue(String questionUId, String value);
}