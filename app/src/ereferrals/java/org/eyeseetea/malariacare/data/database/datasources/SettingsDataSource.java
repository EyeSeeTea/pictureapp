package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.entity.Settings;

public class SettingsDataSource implements ISettingsRepository {

    @Override
    public Settings getSettings() {
        String language = PreferencesState.getInstance().getLanguageCode();
        return new Settings(language);
    }
}
