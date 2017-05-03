package org.eyeseetea.malariacare.data.remote;

import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;
import org.eyeseetea.malariacare.network.ServerAPIController;


public class ProgramDataSource implements IProgramRepository {
    @Override
    public Program getUserProgram(Credentials userCredentials)
            throws PullConversionException, NetworkException {
        return ServerAPIController.getUserProgram(userCredentials);
    }

    @Override
    public void saveUserProgramId(Program program) {

    }

    @Override
    public Long getUserProgramId() {
        return null;
    }
}
