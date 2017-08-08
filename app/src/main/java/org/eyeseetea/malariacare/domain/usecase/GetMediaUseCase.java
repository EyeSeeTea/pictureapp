package org.eyeseetea.malariacare.domain.usecase;

import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.executors.IMainExecutor;
import org.eyeseetea.malariacare.domain.entity.Media;

import java.util.ArrayList;
import java.util.List;

public class GetMediaUseCase implements UseCase {
    private Callback mCallback;
    private IMainExecutor mMainExecutor;
    private IAsyncExecutor mAsyncExecutor;
    private MediaRepository mMediaRepository;

    public GetMediaUseCase(
            IMainExecutor mainExecutor,
            IAsyncExecutor asyncExecutor,
            MediaRepository mediaRepository) {
        mMainExecutor = mainExecutor;
        mAsyncExecutor = asyncExecutor;
        mMediaRepository = mediaRepository;
    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        final List<Media> medias = mMediaRepository.getAllMedia();
        if (medias != null && !medias.isEmpty()) {
            mMainExecutor.run(new Runnable() {
                @Override
                public void run() {
                    mCallback.onSuccess(medias);
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
        void onSuccess( List<Media> medias);

        void onError();
    }
}