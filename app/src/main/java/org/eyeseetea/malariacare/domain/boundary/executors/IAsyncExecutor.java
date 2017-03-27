package org.eyeseetea.malariacare.domain.boundary.executors;

import org.eyeseetea.malariacare.domain.usecase.UseCase;

public interface IAsyncExecutor {
    void run(final UseCase useCase);
}