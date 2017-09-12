package org.eyeseetea.malariacare.data.database.datasources;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.entity.Settings;

import java.util.Locale;

public class SettingsDataSource implements ISettingsRepository {
    @Override
    public Settings getSettings() {
        String systemLanguage = getCurrentLocale().getLanguage();
        String currentLanguage = PreferencesState.getInstance().getLanguageCode();

        return new Settings(systemLanguage, currentLanguage);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return Resources.getSystem().getConfiguration().locale;
        }
    }
}
