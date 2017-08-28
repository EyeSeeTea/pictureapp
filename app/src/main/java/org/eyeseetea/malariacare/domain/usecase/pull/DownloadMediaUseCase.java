package org.eyeseetea.malariacare.domain.usecase.pull;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.io.IFileDownloader;
import org.eyeseetea.malariacare.domain.boundary.repositories.IProgramRepository;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.exception.FileDownloadException;
import org.eyeseetea.malariacare.domain.usecase.UseCase;
import org.eyeseetea.sdk.common.FileUtils;

import java.util.List;

public class DownloadMediaUseCase implements UseCase {
    public interface Callback {
        void onError(FileDownloadException ex);

        void onSuccess(int syncedFiles);

        void showDownloadProgress(boolean value);
    }

    private Callback mCallback;
    private IAsyncExecutor mAsyncExecutor;
    private IFileDownloader mFileDownloader;
    private IConnectivityManager mConnectivityManager;
    private IProgramRepository mProgramRepository;
    private MediaRepository mMediaRepository;

    public DownloadMediaUseCase(
            IAsyncExecutor asyncExecutor,
            IFileDownloader fileDownloader,
            IConnectivityManager connectivityManager,
            IProgramRepository programRepository,
            MediaRepository mediaRepository) {
        mAsyncExecutor = asyncExecutor;
        mFileDownloader = fileDownloader;
        mConnectivityManager = connectivityManager;
        mProgramRepository = programRepository;
        mMediaRepository = mediaRepository;

    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }

    @Override
    public void run() {
        System.out.println("Running DownloadMediaUseCase");

        final List<Media> currentMedias = mMediaRepository.getAll();

        if (mConnectivityManager.isDeviceOnline()) {
            if(mFileDownloader.isFileDownloaderIProgress()) {
                System.out.println("File downloader is already downloading");
                return;
            }
            mFileDownloader.download(currentMedias,
                    PreferencesState.getInstance().getDriveRootFolderUid(),
                    mProgramRepository.getUserProgram().getCode(),
                    new IFileDownloader.Callback() {
                        @Override
                        public void onError(FileDownloadException ex) {
                            mCallback.onError(ex);
                            mFileDownloader.changeFileDownloaderIProgress(false);
                            mCallback.showDownloadProgress(false);
                        }

                        @Override
                        public void showDownloadProgress(boolean value) {
                            mCallback.showDownloadProgress(value);
                        }

                        @Override
                        public void onSuccess(List<Media> syncMedias) {
                            int numOfSyncedFiles = saveDownloadedMedia(syncMedias);

                            removeNotDownloadedMedia(syncMedias, currentMedias);
                            mCallback.onSuccess(numOfSyncedFiles);
                            mFileDownloader.changeFileDownloaderIProgress(false);
                            mCallback.showDownloadProgress(false);
                        }

                    });
        } else {
            System.out.println(this.getClass().getSimpleName()
                    + ": No wifi connection available. Media will not be synced");
            mFileDownloader.changeFileDownloaderIProgress(false);
        }
    }

    private int saveDownloadedMedia(List<Media> syncMedias) {
        int numOfSyncedFiles = 0;
        for (Media syncMedia : syncMedias) {
            if (mMediaRepository.existMedia(syncMedia)) {
                mMediaRepository.update(syncMedia);
            } else {
                numOfSyncedFiles++;
                mMediaRepository.save(syncMedia);
            }
        }
        return numOfSyncedFiles;
    }

    private void removeNotDownloadedMedia(List<Media> downloadedMedia,
            List<Media> persistentMedia) {
        for (Media localMedia : persistentMedia) {
            boolean isRemoved = true;
            for (Media driveMedia : downloadedMedia) {
                if (driveMedia.getResourceUrl().equals(localMedia.getResourceUrl())) {
                    isRemoved = false;
                }
            }
            if (isRemoved) {
                if (localMedia.getResourcePath() != null) {
                    FileUtils.removeFile(localMedia.getResourcePath());
                }
                System.out.println("downloadFile error (file extension not supported)"
                        + localMedia.getResourceUrl());
                mMediaRepository.delete(
                        mMediaRepository.findByUid(localMedia.getResourceUrl()));
            }
        }
    }
}
