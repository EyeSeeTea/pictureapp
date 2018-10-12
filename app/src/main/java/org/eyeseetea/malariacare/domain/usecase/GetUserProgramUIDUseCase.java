package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;

public class GetUserProgramUIDUseCase implements UseCase {
    private IProgramRepository mProgramRepository;
    private Callback mCallback;
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;

    public GetUserProgramUIDUseCase(
            IProgramRepository programRepository,
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor) {
        mProgramRepository = programRepository;
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        final String uid = mProgramRepository.getUserProgram().getId();
        if (uid != null && !uid.isEmpty()) {
            mMainExecutor.run(new Runnable() {
                @Override
                public void run() {
                    mCallback.onSuccess(uid);
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
        void onSuccess(String uid);

        void onError();
    }
}
