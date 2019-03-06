package org.eyeseetea.malariacare.factories;

import org.eyeseetea.malariacare.data.database.datasources.QuestionLocalDataSource;
import org.eyeseetea.malariacare.data.repositories.ProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.IQuestionRepository;
import org.eyeseetea.malariacare.domain.usecase.GetQuestionsByProgramUseCase;
import org.eyeseetea.malariacare.domain.usecase.GetUserProgramUseCase;
import org.eyeseetea.malariacare.presentation.executors.AsyncExecutor;
import org.eyeseetea.malariacare.presentation.executors.UIThreadExecutor;

public class MetadataFactory {
    protected IMainExecutor mainExecutor = new UIThreadExecutor();
    protected IAsyncExecutor asyncExecutor = new AsyncExecutor();

    public GetUserProgramUseCase getUserProgramUseCase() {
        IProgramRepository programRepository = new ProgramRepository();

        return new GetUserProgramUseCase(
                programRepository, mainExecutor, asyncExecutor);
    }

    public GetQuestionsByProgramUseCase getQuestionsByProgramUseCase() {
        IQuestionRepository questionRepository = new QuestionLocalDataSource();

        return new GetQuestionsByProgramUseCase(
                mainExecutor, asyncExecutor, questionRepository);
    }
}
