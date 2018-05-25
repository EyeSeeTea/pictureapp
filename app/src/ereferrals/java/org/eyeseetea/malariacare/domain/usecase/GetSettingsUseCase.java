package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.boundary.repositories.IMediaRepository;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.entity.Settings;

import java.util.List;

public class GetSettingsUseCase implements UseCase {
    private Callback mCallback;
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private ISettingsRepository mSettingsRepository;

    public GetSettingsUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            ISettingsRepository settingsRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mSettingsRepository = settingsRepository;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        final Settings setting = mSettingsRepository.getSettings();

        mMainExecutor.run(new Runnable() {
            @Override
            public void run() {
                mCallback.onSuccess(setting);
            }
        });
    }

    public interface Callback {
        void onSuccess(Settings setting);
    }
}