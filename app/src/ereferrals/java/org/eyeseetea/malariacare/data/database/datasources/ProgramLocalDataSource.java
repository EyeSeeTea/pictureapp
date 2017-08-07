package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Program;


public class ProgramLocalDataSource implements IProgramRepository {

    @Override
    public void saveUserProgramId(Program program) {
        ProgramDB databaseProgramDB =
                ProgramDB.findByName(program.getCode());
        PreferencesEReferral.saveUserProgramId(databaseProgramDB.getId_program());
    }

    @Override
    public Program getUserProgram() {
        ProgramDB databaseProgramDB =
                ProgramDB.getProgram(
                        PreferencesEReferral.getUserProgramId());
        Program program = new Program(databaseProgramDB.getName(), databaseProgramDB.getUid());
        return program;
    }


}
