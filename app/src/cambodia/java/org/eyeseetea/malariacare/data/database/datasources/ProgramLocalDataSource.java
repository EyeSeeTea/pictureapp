package org.eyeseetea.malariacare.data.database.datasources;


import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Program;


public class ProgramLocalDataSource implements IProgramRepository {


    @Override
    public void saveUserProgramId(Program program) {

    }

    @Override
    public Program getUserProgram() {
        org.eyeseetea.malariacare.data.database.model.ProgramDB programDB =
                org.eyeseetea.malariacare.data.database.model.ProgramDB.getFirstProgram();
        if (programDB == null) {
            return null;
        }
        Program program = new Program(programDB.getName(), programDB.getUid());
        return program;
    }

    @Override
    public boolean checkLastDownloadedProgramMedia(String program) {
        return false;
    }

    @Override
    public void saveLastDownloadedProgramMedia(String code) {

    }

    @Override
    public void removeProgramMedia() {

    }
}
