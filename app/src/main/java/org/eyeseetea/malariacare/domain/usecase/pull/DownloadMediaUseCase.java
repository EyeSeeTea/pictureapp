package org.eyeseetea.malariacare.domain.usecase.pull;

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

        void acquireGooglePlayServices();

        void onSuccess(int syncedFiles);
    }

    //TODO  Change by the correct uid
    private static final String rootUid = "0B7TXlu17DcAbNHNvRUxnSDd4LUk";
    private static final String TAG = "DownloadMediaUseCase";
    private Callback mCallback;
    private IAsyncExecutor mAsyncExecutor;
    private IFileDownloader mFileDownloader;
    private IConnectivityManager mConnectivityManager;
    private MediaRepository mMediaRepository;

    public DownloadMediaUseCase(
            IAsyncExecutor asyncExecutor,
            IFileDownloader fileDownloader,
            IConnectivityManager connectivityManager,
            MediaRepository mediaRepository,
            Callback callback) {
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
        System.out.println("Running DownloadMediaUseCase");
        IFileDownloader.Callback callback = new IFileDownloader.Callback() {
            @Override
            public void onError(FileDownloadException ex) {
                mCallback.onError(ex);
            }

            @Override
            public void acquireGooglePlayServices() {
                mCallback.acquireGooglePlayServices();
            }

            @Override
            public void onSuccess(HashMap<String, String> syncedFiles) {
                int numOfSyncedFiles =  mMediaRepository.updateSyncedFiles(syncedFiles);
                mCallback.onSuccess(numOfSyncedFiles);
            }

            @Override
            public void removeRemotelyDeletedMedia(List<String> uids){
                List<Media> medias = mMediaRepository.getAll();
                for(Media media:medias){
                    //If the media is not stored in the hashMap, the media was removed from drive folder and will be removed from database.
                    if(!uids.contains(media.getResourceUrl())){
                        if(media.getResourcePath()!=null) {
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
                System.out.println("Remove file with uid" + uid);
                mMediaRepository.deleteModel(mMediaRepository.findByUid(uid));
            }
        };

        mFileDownloader.init(PreferencesState.getInstance().getDriveRootFolderUid(),  callback);
        final List<Media> medias = mMediaRepository.getAllNotDownloaded();
        List<String> uids = new ArrayList<>();
        for (Media media : medias) {
            if (!uids.contains(media.getResourceUrl())) {
                uids.add(media.getResourceUrl());
            }
        }

        if (uids != null && !uids.isEmpty()) {
            if (mConnectivityManager.isDeviceOnline()) {
                mFileDownloader.download(uids, callback);
            } else {
                System.out.println(this.getClass().getSimpleName()
                        + ": No wifi connection available. Media will not be synced");
            }
        }
    }
}
