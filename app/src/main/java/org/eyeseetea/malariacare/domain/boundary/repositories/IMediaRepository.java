package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.Media;

import java.util.List;

public interface IMediaRepository {
    List<Media> getAll();

    List<Media> getAllDownloaded();

    void clearMedia();

    String getResourcePathByUid(String id);
}
