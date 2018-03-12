package org.eyeseetea.malariacare.data.database.datasources;


import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.ProgramProgramRelationDB;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Program;

import java.util.ArrayList;
import java.util.List;


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
    public Program getProgramWithId(String programId) {
        ProgramDB programDB = ProgramDB.getProgram(programId);
        Program program = new Program(programDB.getName(), programDB.getUid());
        return program;
    }

    @Override
    public List<Program> getRelatedPrograms(String programId) {
        ProgramDB programDB = ProgramDB.getProgram(programId);
        List<ProgramDB> programDBS = ProgramProgramRelationDB.getRelatedPrograms(
                programDB.getId_program());
        List<Program> programs = new ArrayList<>();
        for (ProgramDB programDB1 : programDBS) {
            programs.add(mapProgramDBToProgram(programDB1));
        }
        return programs;
    }


    private Program mapProgramDBToProgram(ProgramDB programDB) {
        return new Program(programDB.getName(), programDB.getUid());
    }
}
