package org.eyeseetea.malariacare.domain.usecase.pull;

import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.io.IFileDownloader;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.exception.FileDownloadException;
import org.eyeseetea.malariacare.domain.usecase.UseCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DownloadMediaUseCase implements UseCase {
    public interface Callback {
        void onError(FileDownloadException ex);
        void onSuccess(int syncedFiles);
    }

    private Callback mCallback;
    private IAsyncExecutor mAsyncExecutor;
    private IFileDownloader mFileDownloader;
    private IConnectivityManager mConnectivityManager;
    private MediaRepository mMediaRepository;

    public DownloadMediaUseCase(
            IAsyncExecutor asyncExecutor,
            IFileDownloader fileDownloader,
            IConnectivityManager connectivityManager,
            MediaRepository mediaRepository) {
        mAsyncExecutor = asyncExecutor;
        mFileDownloader = fileDownloader;
        mConnectivityManager = connectivityManager;
        mMediaRepository = mediaRepository;

    }

    public void execute(Callback callback) {
        mCallback = callback;
        mAsyncExecutor.run(this);
    }


    @Override
    public void run() {
        List<String> uids = getResourceUrlsToDownload();

        if (uids != null && !uids.isEmpty()) {
            if (mConnectivityManager.isDeviceOnline()) {
                mFileDownloader.download(uids, new IFileDownloader.Callback() {
                    @Override
                    public void onError(FileDownloadException ex) {
                        mCallback.onError(ex);
                    }

                    @Override
                    public void onSuccess(HashMap<String, String> syncedFiles) {
                        int numOfSyncedFiles =  mMediaRepository.updateSyncedFiles(syncedFiles);
                        mCallback.onSuccess(numOfSyncedFiles);
                    }

                    @Override
                    public void onRemove(String uid) {
                        System.out.println("downloadFile error (file extension not supported)" + uid);
                        mMediaRepository.delete(mMediaRepository.findByUid(uid));
                    }
                });
            } else {
                System.out.println(this.getClass().getSimpleName()
                        + ": No wifi connection available. Media will not be synced");
            }
        }
    }

    @NonNull
    private List<String> getResourceUrlsToDownload() {
        final List<Media> medias = mMediaRepository.getAllNotDownloaded();
        List<String> uids = new ArrayList<>();
        for (Media media : medias) {
            if (!uids.contains(media.getResourceUrl())) {
                uids.add(media.getResourceUrl());
            }
        }
        return uids;
    }
}
