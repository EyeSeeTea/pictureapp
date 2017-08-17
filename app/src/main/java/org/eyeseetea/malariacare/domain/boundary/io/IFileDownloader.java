package org.eyeseetea.malariacare.domain.boundary.io;

import org.eyeseetea.malariacare.domain.exception.FileDownloadException;
import org.eyeseetea.malariacare.domain.usecase.pull.DownloadMediaUseCase;

import java.util.HashMap;
import java.util.List;

public interface IFileDownloader {

    void download(List<String> uids, DownloadMediaUseCase.Callback callback);
}