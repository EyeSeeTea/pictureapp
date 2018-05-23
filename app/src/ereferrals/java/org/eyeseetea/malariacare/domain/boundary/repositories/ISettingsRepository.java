package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.MediaListMode;
import org.eyeseetea.malariacare.domain.entity.Settings;

public interface ISettingsRepository {
    Settings getSettings();

    void saveSettings(Settings settings);
}
