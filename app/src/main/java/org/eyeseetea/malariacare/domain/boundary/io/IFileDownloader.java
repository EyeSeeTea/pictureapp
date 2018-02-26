package org.eyeseetea.malariacare.domain.boundary.io;

import org.eyeseetea.malariacare.domain.entity.Media;

import java.util.List;

public interface IFileDownloader {
    interface Callback {
        void onSuccess(List<Media> syncedFiles);

        void onError(Throwable throwable);
    }

    void download(List<Media> currentMedias, String rootUid, String program, Callback callback);

    boolean isFileDownloaderIProgress();

    void changeFileDownloaderIProgress(boolean inProgress);
}