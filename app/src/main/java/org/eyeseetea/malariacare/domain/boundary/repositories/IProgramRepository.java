package org.eyeseetea.malariacare.domain.boundary.repositories;


import org.eyeseetea.malariacare.domain.entity.Program;

public interface IProgramRepository {

    void saveUserProgramId(Program program);

    Program getUserProgram();
}
