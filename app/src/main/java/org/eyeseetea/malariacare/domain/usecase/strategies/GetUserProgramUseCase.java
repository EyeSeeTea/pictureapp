package org.eyeseetea.malariacare.domain.usecase.strategies;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.usecase.UseCase;

public class GetUserProgramUseCase implements UseCase {
    private IProgramRepository mProgramLocalDataSource;
    private Callback mCallback;
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;

    public GetUserProgramUseCase(
            IProgramRepository programLocalDataSource,
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor) {
        mProgramLocalDataSource = programLocalDataSource;
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }


    @Override
    public void run() {
        final Program program = mProgramLocalDataSource.getUserProgram();
        if (program != null) {
            mMainExecutor.run(new Runnable() {
                @Override
                public void run() {
                    mCallback.onSuccess(program);
                }
            });
        } else {
            mMainExecutor.run(new Runnable() {
                @Override
                public void run() {
                    mCallback.onError();
                }
            });
        }
    }

    public interface Callback {
        void onSuccess(Program program);

        void onError();
    }
}
