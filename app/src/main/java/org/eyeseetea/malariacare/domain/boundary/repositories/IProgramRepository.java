package org.eyeseetea.malariacare.domain.boundary.repositories;


import org.eyeseetea.malariacare.domain.entity.Program;

import java.util.List;

public interface IProgramRepository {

    void saveUserProgramId(Program program);

    Program getUserProgram();

    Program getProgramWithId(String programId);

    List<Program> getRelatedPrograms(String programId);
}
