package org.eyeseetea.malariacare.data.database.datasources;


import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Program;


public class ProgramLocalDataSource implements IProgramRepository {


    @Override
    public void saveUserProgramId(Program program) {

    }

    @Override
    public Program getUserProgram() {
        org.eyeseetea.malariacare.data.database.model.Program programDB =
                org.eyeseetea.malariacare.data.database.model.Program.getFirstProgram();
        if (programDB == null) {
            return null;
        }
        Program program = new Program(programDB.getName(), programDB.getUid());
        return program;
    }
}
