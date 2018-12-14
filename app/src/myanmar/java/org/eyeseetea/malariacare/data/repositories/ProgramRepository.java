package org.eyeseetea.malariacare.data.repositories;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Program;

import java.util.List;


public class ProgramRepository implements IProgramRepository {


    @Override
    public void saveUserProgramId(Program program) {

    }


    @Override
    public Program getUserProgram() {
        ProgramDB programDB =
                ProgramDB.findByUID(
                        PreferencesState.getInstance().getContext().getString(
                                R.string.malariaProgramUID));
        if (programDB == null) {
            return null;
        }
        Program program = new Program(programDB.getName(), programDB.getUid());
        return program;
    }

    @Override
    public Program getProgramWithId(String programId) {
        return null;
    }

    @Override
    public List<Program> getRelatedPrograms(String programId) {
        return null;
    }
}
