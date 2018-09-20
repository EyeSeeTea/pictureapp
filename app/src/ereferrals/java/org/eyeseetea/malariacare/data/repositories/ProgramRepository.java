package org.eyeseetea.malariacare.data.repositories;

import org.eyeseetea.malariacare.data.database.datasources.ProgramLocalDataSource;
import org.eyeseetea.malariacare.data.remote.ProgramWSDataSource;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Program;

import java.util.List;


public class ProgramRepository implements IProgramRepository {

    private ProgramLocalDataSource mProgramLocalDataSource = new ProgramLocalDataSource();
    private ProgramWSDataSource mProgramRemoteDataSource = new ProgramWSDataSource();

    @Override
    public void saveUserProgramId(Program program) {
        mProgramLocalDataSource.saveUserProgramId(program);
    }

    @Override
    public Program getUserProgram() {
        Program program = mProgramLocalDataSource.getUserProgram();

        if (program == null) {
            program = mProgramRemoteDataSource.getUserProgram();
        }

        return program;
    }

    @Override
    public Program getProgramWithId(String programId) {
        return mProgramLocalDataSource.getProgramWithId(programId);
    }

    @Override
    public List<Program> getRelatedPrograms(String programId) {
        return mProgramLocalDataSource.getRelatedPrograms(programId);
    }
}
