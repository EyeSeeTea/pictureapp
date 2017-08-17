package org.eyeseetea.malariacare.domain.boundary.io;

import org.eyeseetea.malariacare.domain.exception.FileDownloadException;

import java.util.HashMap;
import java.util.List;

public interface IFileDownloader {

    interface Callback {
        void onSuccess(HashMap<String, String>syncedFiles);

        void onError(FileDownloadException throwable);

        void onRemove(String uid);

        void acquireGooglePlayServices();
    }
    void download(List<String> uids, Callback callback);
}