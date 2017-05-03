package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;

public interface IProgramRepository {
    Program getUserProgram(Credentials userCredentials)
            throws PullConversionException, NetworkException;

    void saveUserProgramId(Program program);

    Long getUserProgramId();
}
