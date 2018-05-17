package org.eyeseetea.malariacare.domain.boundary;

import org.eyeseetea.malariacare.domain.entity.Settings;

public interface IMediaModeRepository {

    Settings.MediaListMode getMediaListMode();

    void saveMediaListMode(Settings.MediaListMode mediaListMode);
}
