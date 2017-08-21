package org.eyeseetea.malariacare.domain.boundary.io;

import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.exception.FileDownloadException;

import java.util.List;

public interface IFileDownloader {
    interface Callback {
        void onSuccess(List<Media> syncedFiles);

        void onError(FileDownloadException throwable);
    }

    void download(String rootUid, String program, Callback callback);
}