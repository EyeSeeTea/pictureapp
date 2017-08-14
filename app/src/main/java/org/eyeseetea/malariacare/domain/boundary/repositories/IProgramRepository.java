package org.eyeseetea.malariacare.domain.boundary.repositories;


import org.eyeseetea.malariacare.domain.entity.Program;

public interface IProgramRepository {

    void saveUserProgramId(Program program);

    Program getUserProgram();

    boolean checkLastDownloadedProgramMedia(String program);

    void saveLastDownloadedProgramMedia(String code);

    void removeProgramMedia();
}
