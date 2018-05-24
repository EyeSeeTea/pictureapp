package org.eyeseetea.malariacare.domain.boundary.repositories;
import org.eyeseetea.malariacare.domain.entity.Settings;

public interface ISettingsRepository {

    enum MediaListMode { GRID, LIST }

    Settings getSettings();

    void saveSettings(Settings settings);
}
