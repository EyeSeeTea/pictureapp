package org.eyeseetea.malariacare.data.database;

import org.eyeseetea.malariacare.data.database.model.Program;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Credentials;
import org.eyeseetea.malariacare.domain.exception.NetworkException;
import org.eyeseetea.malariacare.domain.exception.PullConversionException;


public class ProgramLocalDataSource implements IProgramRepository {
    @Override
    public Program getUserProgram(Credentials userCredentials)
            throws PullConversionException, NetworkException {
        return null;
    }

    @Override
    public void saveUserProgramId(Program program) {
        PreferencesEReferral.saveUserProgramId(program.getId_program());
    }

    @Override
    public Long getUserProgramId() {
        return PreferencesEReferral.getUserProgramId();
    }

}
