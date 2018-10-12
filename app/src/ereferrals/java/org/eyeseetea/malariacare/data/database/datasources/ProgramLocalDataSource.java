package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.ProgramProgramRelationDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.domain.entity.Program;

import java.util.ArrayList;
import java.util.List;

public class ProgramLocalDataSource {
    public void saveUserProgramId(Program program) {
        ProgramDB databaseProgramDB =
                ProgramDB.findByName(program.getCode());

        PreferencesEReferral.saveUserProgramId(databaseProgramDB.getId_program());
    }

    public Program getUserProgram() {
        Program program = null;

        ProgramDB databaseProgramDB = ProgramDB.getProgram(PreferencesEReferral.getUserProgramId());

        if (databaseProgramDB != null) {
            program = new Program(databaseProgramDB.getName(), databaseProgramDB.getUid());
        }

        return program;
    }

    public Program getProgramWithId(String programId) {
        ProgramDB programDB = ProgramDB.getProgram(programId);
        Program program = new Program(programDB.getName(), programDB.getUid());
        return program;
    }

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
