package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Program;

import java.util.List;

public class HasToGenerateStockProgramUseCase implements UseCase {
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private IProgramRepository mProgramRepository;
    private Callback mCallback;
    private String mProgramId;

    public HasToGenerateStockProgramUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            IProgramRepository programRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mProgramRepository = programRepository;
    }

    public void execute(String programId, Callback callback) {
        mProgramId = programId;
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        List<Program> programs = mProgramRepository.getRelatedPrograms(mProgramId);
        for (Program program : programs) {
            if (program.getId().equals(PreferencesState.getInstance().getContext().getString(
                    R.string.stock_program_uid))) {
                notifyHasToCreateStock(true);
                return;
            }
        }
        notifyHasToCreateStock(false);
    }

    private void notifyHasToCreateStock(final boolean create) {
        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.hasToCreateStock(create);
            }
        });
    }

    public interface Callback {
        void hasToCreateStock(boolean create);
    }
}
