package org.eyeseetea.malariacare.data.database;


import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Program;


public class ProgramLocalDataSource implements IProgramRepository {


    @Override
    public void saveUserProgramId(Program program) {

    }

    @Override
    public Long getUserProgramId() {
        org.eyeseetea.malariacare.data.database.model.Program program =
                org.eyeseetea.malariacare.data.database.model.Program.getFirstProgram();
        if (program == null) {
            return 0l;
        }
        return program.getId_program();
    }

    @Override
    public String getUserProgramUID() {
        org.eyeseetea.malariacare.data.database.model.Program program =
                org.eyeseetea.malariacare.data.database.model.Program.getFirstProgram();
        if (program == null) {
            return "";
        }
        return program.getUid();
    }
}
