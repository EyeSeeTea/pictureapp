package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.InvalidLoginAttempts;

public interface IInvalidLoginAttemptsRepository {

    InvalidLoginAttempts getInvalidLoginAttempts();

    void saveInvalidLoginAttempts(InvalidLoginAttempts invalidLoginAttempts);
}
