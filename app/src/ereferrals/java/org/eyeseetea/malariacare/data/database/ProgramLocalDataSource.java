package org.eyeseetea.malariacare.data.database;

import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Program;


public class ProgramLocalDataSource implements IProgramRepository {

    @Override
    public void saveUserProgramId(Program program) {
        org.eyeseetea.malariacare.data.database.model.Program databaseProgram =
                org.eyeseetea.malariacare.data.database.model.Program.findByName(program.getCode());
        PreferencesEReferral.saveUserProgramId(databaseProgram.getId_program());
    }

    @Override
    public Long getUserProgramId() {
        return PreferencesEReferral.getUserProgramId();
    }

    @Override
    public String getUserProgramUID() {
        return org.eyeseetea.malariacare.data.database.model.Program.getProgram(
                getUserProgramId()).getUid();
    }

}
