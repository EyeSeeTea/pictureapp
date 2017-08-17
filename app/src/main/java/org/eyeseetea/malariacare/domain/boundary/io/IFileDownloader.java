package org.eyeseetea.malariacare.domain.boundary.io;

import org.eyeseetea.malariacare.strategies.DashboardActivityStrategy;

import java.util.List;

public interface IFileDownloader {
    void download(List<String> uids, DashboardActivityStrategy.Callback callback);
}
