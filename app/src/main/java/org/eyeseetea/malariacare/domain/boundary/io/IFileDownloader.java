package org.eyeseetea.malariacare.domain.boundary.io;

import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.exception.FileDownloadException;

import java.util.HashMap;
import java.util.List;

public interface IFileDownloader {
    interface Callback {
        void onSuccess(HashMap<String, String> syncedFiles);

        void onError(FileDownloadException throwable);

        void removeRemotelyDeletedMedia(List<String> uids);

        void remove(String uid);

        void save(Media media);
    }

    void download(List<String> uids, Callback callback);

    void init(String rootUid, Callback callback);
}