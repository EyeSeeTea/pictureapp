package org.eyeseetea.malariacare.domain.boundary.ports;

import org.eyeseetea.malariacare.strategies.DashboardActivityStrategy;

import java.util.List;

public interface IFileDownloader {
    void download(List<String> uids, DashboardActivityStrategy.Callback callback);

    void init(String rootUid, DashboardActivityStrategy.Callback callback);
}
