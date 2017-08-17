package org.eyeseetea.malariacare.domain.usecase.pull;

import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.io.IFileDownloader;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.usecase.UseCase;
import org.eyeseetea.malariacare.strategies.DashboardActivityStrategy;

import java.util.ArrayList;
import java.util.List;

public class DownloadMediaUseCase implements UseCase {


    private DashboardActivityStrategy.Callback mCallback;
    private IAsyncExecutor mAsyncExecutor;
    private IFileDownloader mFileDownloader;
    private IConnectivityManager mConnectivityManager;
    private MediaRepository mMediaRepository;

    public DownloadMediaUseCase(
            IAsyncExecutor asyncExecutor,
            IFileDownloader fileDownloader,
            IConnectivityManager connectivityManager,
            MediaRepository mediaRepository,
            DashboardActivityStrategy.Callback callback) {
        mAsyncExecutor = asyncExecutor;
        mFileDownloader = fileDownloader;
        mConnectivityManager = connectivityManager;
        mMediaRepository = mediaRepository;
        mCallback = callback;
    }

    public void execute() {
        mAsyncExecutor.run(this);
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    @Override
    public void run() {
        final List<Media> medias = mMediaRepository.getAllNotDownloaded();
        List<String> uids = new ArrayList<>();
        for (Media media : medias) {
            if (!uids.contains(media.getResourceUrl())) {
                uids.add(media.getResourceUrl());
            }
        }

        if (uids != null && !uids.isEmpty()) {
            if (mConnectivityManager.isDeviceOnline()) {
                mFileDownloader.download(uids, mCallback);
            } else {
                System.out.println(this.getClass().getSimpleName()
                        + ": No wifi connection available. Media will not be synced");
            }
        }
    }
}
