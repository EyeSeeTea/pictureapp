package org.eyeseetea.malariacare.domain.boundary.io;

import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.exception.FileDownloadException;

import java.util.List;

public interface IFileDownloader {
    interface Callback {
        void onSuccess(List<Media> syncedFiles);

        void onError(FileDownloadException throwable);

        void showDownloadProgress(boolean value);
    }

    void download(List<Media> currentMedias, String rootUid, String program, Callback callback);

    boolean isFileDownloaderIProgress();

    void changeFileDownloaderIProgress(boolean inProgress);
}