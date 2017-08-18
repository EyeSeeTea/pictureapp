package org.eyeseetea.malariacare.domain.usecase.pull;

import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.boundary.io.IFileDownloader;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.exception.FileDownloadException;
import org.eyeseetea.malariacare.domain.usecase.UseCase;
import org.eyeseetea.sdk.common.FileUtils;

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
        System.out.println("Running DownloadMediaUseCase");
        IFileDownloader.Callback callback = new IFileDownloader.Callback() {
            @Override
            public void onError(FileDownloadException ex) {
                mCallback.onError(ex);
            }

            @Override
            public void onSuccess(HashMap<String, String> syncedFiles) {
                int numOfSyncedFiles = mMediaRepository.updateSyncedFiles(syncedFiles);
                mCallback.onSuccess(numOfSyncedFiles);
            }

            @Override
            public void removeRemotelyDeletedMedia(List<String> uids) {
                List<Media> medias = mMediaRepository.getAll();
                for (Media media : medias) {
                    //If the media is not stored in the hashMap, the media was removed from drive
                    // folder and will be removed from database.
                    if (!uids.contains(media.getResourceUrl())) {
                        if (media.getResourcePath() != null) {
                            FileUtils.removeFile(media.getResourcePath());
                        }
                        remove(media.getResourceUrl());
                    }
                }
                mCallback.onSuccess(0);
            }

            @Override
            public void save(Media media) {
                mMediaRepository.updateNotDownloadedMedia(media);
            }

            @Override
            public void remove(String uid) {
                System.out.println("downloadFile error (file extension not supported)" + uid);
                mMediaRepository.delete(mMediaRepository.findByUid(uid));
            }
        };

        mFileDownloader.init(PreferencesState.getInstance().getDriveRootFolderUid(), callback);
        final List<String> uids = getResourceUrlsToDownload();

        if (uids != null && !uids.isEmpty()) {
            if (mConnectivityManager.isDeviceOnline()) {
                mFileDownloader.download(uids, callback);
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
