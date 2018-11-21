package org.eyeseetea.malariacare.strategies;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.EditText;

import org.eyeseetea.malariacare.BuildConfig;
import org.eyeseetea.malariacare.EyeSeeTeaApplication;
import org.eyeseetea.malariacare.LoginActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.SettingsActivity;
import org.eyeseetea.malariacare.data.database.datasources.SettingsDataSource;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.repositories.ISettingsRepository;
import org.eyeseetea.malariacare.domain.entity.Language;
import org.eyeseetea.malariacare.domain.usecase.GetAllLanguagesUseCase;
import org.eyeseetea.malariacare.domain.usecase.LogoutUseCase;
import org.eyeseetea.malariacare.factories.AuthenticationFactoryStrategy;
import org.eyeseetea.malariacare.factories.LanguagesFactory;
import org.eyeseetea.malariacare.layout.listeners.LogoutAndLoginRequiredOnPreferenceClickListener;
import org.eyeseetea.malariacare.services.PushService;
import org.eyeseetea.malariacare.services.strategies.PushServiceStrategy;
import org.eyeseetea.malariacare.utils.CustomFontStyles;
import org.eyeseetea.malariacare.utils.LockScreenStatus;
import org.eyeseetea.malariacare.utils.Utils;

import java.util.List;

public class SettingsActivityStrategy extends ASettingsActivityStrategy {

    LogoutAndLoginRequiredOnPreferenceClickListener logoutAndloginRequiredOnPreferenceClickListener;
    LogoutUseCase mLogoutUseCase;

    public SettingsActivityStrategy(SettingsActivity settingsActivity) {
        super(settingsActivity);

        logoutAndloginRequiredOnPreferenceClickListener =
                new LogoutAndLoginRequiredOnPreferenceClickListener(settingsActivity);
    }

    private BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.d(TAG, "Screen off");
                showLogin();
            }
        }
    };

    @Override
    public void onStop() {
        applicationdidenterbackground();
        LocalBroadcastManager.getInstance(settingsActivity).unregisterReceiver(pushReceiver);
        if (EyeSeeTeaApplication.getInstance().isAppInBackground() && !LockScreenStatus.isPatternSet(
                settingsActivity)) {
            ActivityCompat.finishAffinity(settingsActivity);
        }
    }
    public void applicationdidenterbackground() {
        if (!EyeSeeTeaApplication.getInstance().isWindowFocused()) {
            EyeSeeTeaApplication.getInstance().setAppInBackground(true);
        }
    }

    @Override
    public void setupPreferencesScreen(PreferenceScreen preferenceScreen) {
        PreferenceCategory preferenceCategory =
                (PreferenceCategory) preferenceScreen.findPreference(
                        settingsActivity.getResources().getString(R.string.pref_cat_server));
        preferenceCategory.removePreference(preferenceScreen.findPreference(
                settingsActivity.getResources().getString(R.string.org_unit)));

        Preference drivePreference = preferenceScreen.findPreference(
                settingsActivity.getResources().getString(R.string.drive_key));
        Preference metadataPreference = preferenceScreen.findPreference(
                settingsActivity.getResources().getString(R.string.check_metadata_key));
        settingsActivity.translatePreferenceString(drivePreference);
        settingsActivity.translatePreferenceString(metadataPreference);
        if (!PreferencesState.getInstance().isDevelopOptionActive()
                || !BuildConfig.developerOptions) {
            preferenceCategory.removePreference(preferenceScreen.findPreference(
                    settingsActivity.getResources().getString(R.string.drive_key)));
            preferenceCategory.removePreference(metadataPreference);
        }

        Preference serverUrlPreference = (Preference) settingsActivity.findPreference(
                settingsActivity.getResources().getString(R.string.web_service_url));
        serverUrlPreference.setOnPreferenceClickListener(
                getOnPreferenceClickListener());


        initProgramConfigurationSettings(preferenceScreen);
    }

    private void initProgramConfigurationSettings(PreferenceScreen preferenceScreen) {
        EditTextPreference programUrl = (EditTextPreference) preferenceScreen.findPreference(
                preferenceScreen.getContext().getString(R.string.program_configuration_url));
        EditTextPreference programPass = (EditTextPreference) preferenceScreen.findPreference(
                preferenceScreen.getContext().getString(R.string.program_configuration_pass));
        EditTextPreference programUser = (EditTextPreference) preferenceScreen.findPreference(
                preferenceScreen.getContext().getString(R.string.program_configuration_user));


        ISettingsRepository settingsRepository = new SettingsDataSource(preferenceScreen.getContext());

        programUrl.setOnPreferenceChangeListener(new onCredentialsChangeListener());
        programUrl.setText(settingsRepository.getSettings().getProgramUrl());
        programUrl.setSummary(settingsRepository.getSettings().getProgramUrl());

        programUser.setOnPreferenceChangeListener(new onCredentialsChangeListener());
        programUser.setText(settingsRepository.getSettings().getUser());
        programUser.setSummary(settingsRepository.getSettings().getUser());

        programPass.setOnPreferenceChangeListener(new onPasswordChangeListener(programPass.getEditText()));
        programPass.setText(settingsRepository.getSettings().getPass());
        String pass = settingsRepository.getSettings().getPass();
        String pref = programPass.getEditText().getTransformationMethod().getTransformation(pass.toString(), programPass.getEditText()).toString();
        programPass.setSummary(pref);
    }

    private class onCredentialsChangeListener implements Preference.OnPreferenceChangeListener{
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean result = isNotEmpty(newValue);
            if(result){
                preference.setSummary(newValue.toString());
            }
            return result;
        }
    }

    private class onPasswordChangeListener implements Preference.OnPreferenceChangeListener{
        EditText mEditText;
        public onPasswordChangeListener(EditText editText) {
            mEditText = editText;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean result = isNotEmpty(newValue);
            if(result){
                String pref = mEditText.getTransformationMethod().getTransformation(newValue.toString(), mEditText).toString();
                preference.setSummary(pref);
            }
            return result;
        }
    }

    @Override
    public Preference.OnPreferenceClickListener getOnPreferenceClickListener() {
        return logoutAndloginRequiredOnPreferenceClickListener;
    }

    @Override
    public Preference.OnPreferenceChangeListener getOnPreferenceChangeListener() {
        return null;
    }

    public static boolean showAnnouncementOnBackPressed() {
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(
                PreferencesState.getInstance().getContext().getString(R.string.developer_option))) {
            settingsActivity.restartActivity();
        }
    }


    @Override
    public void onStart() {
        applicationWillEnterForeground();
        LocalBroadcastManager.getInstance(settingsActivity).registerReceiver(pushReceiver,
                new IntentFilter(PushService.class.getName()));
    }
    private void applicationWillEnterForeground() {
        if (EyeSeeTeaApplication.getInstance().isAppInBackground()) {
            EyeSeeTeaApplication.getInstance().setAppInBackground(false);
        }
    }

    @Override
    public void onBackPressed() {
        EyeSeeTeaApplication.getInstance().setIsBackPressed(true);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        EyeSeeTeaApplication.getInstance().setIsWindowFocused(hasFocus);

        if (EyeSeeTeaApplication.getInstance().isBackPressed() && !hasFocus) {
            EyeSeeTeaApplication.getInstance().setIsBackPressed(false);
            EyeSeeTeaApplication.getInstance().setIsWindowFocused(true);
        }
    }

    @Override
    public void addExtraPreferences() {
        Preference webServiceUrlPreference = settingsActivity.findPreference(
                settingsActivity.getString(R.string.web_service_url));
        settingsActivity.bindPreferenceSummaryToValue(webServiceUrlPreference);
        settingsActivity.translatePreferenceString(webServiceUrlPreference);
        Preference webViewPreference = settingsActivity.findPreference(
                settingsActivity.getString(R.string.web_view_url));
        settingsActivity.bindPreferenceSummaryToValue(webViewPreference);
        settingsActivity.translatePreferenceString(webViewPreference);
        settingsActivity.translatePreferenceString(settingsActivity.findPreference(
                settingsActivity.getString(R.string.program_configuration_url)));
        settingsActivity.translatePreferenceString(settingsActivity.findPreference(
                settingsActivity.getString(R.string.program_configuration_user)));
        settingsActivity.translatePreferenceString(settingsActivity.findPreference(
                settingsActivity.getString(R.string.program_configuration_pass)));
        settingsActivity.translatePreferenceString(settingsActivity.findPreference(
                settingsActivity.getString(R.string.allow_media_download_3g_key)));
        Preference developerPreference = settingsActivity.findPreference(
                settingsActivity.getString(R.string.developer_option));
        settingsActivity.translatePreferenceString(developerPreference);
        developerPreference.setSummary(
                Utils.getInternationalizedString(R.string.developer_option_summary,
                        settingsActivity));
        Preference elementsPreference = settingsActivity.findPreference(
                settingsActivity.getString(R.string.activate_elements_key));
        settingsActivity.translatePreferenceString(elementsPreference);
        elementsPreference.setSummary(
                Utils.getInternationalizedString(R.string.activate_elements, settingsActivity));
    }

    @Override
    public void onCreate() {
        mLogoutUseCase = new AuthenticationFactoryStrategy().getLogoutUseCase(settingsActivity);
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        settingsActivity.registerReceiver(mScreenOffReceiver, screenStateFilter);
    }

    private void showLogin() {
        if (!LockScreenStatus.isPatternSet(settingsActivity)) {
            Intent loginIntent = new Intent(settingsActivity, LoginActivity.class);
            loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            settingsActivity.startActivity(loginIntent);
        }
    }

    @Override
    public void onDestroy() {
        settingsActivity.unregisterReceiver(mScreenOffReceiver);
    }

    private BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showLoginIfConfigFileObsolete(intent);
        }

        private void showLoginIfConfigFileObsolete(Intent intent) {
            if (intent.getBooleanExtra(PushServiceStrategy.SHOW_LOGIN, false)) {
                showLogin();
            }
        }
    };

    @Override
    public void addFontStyleEntries(List<String> entries, List<String> entryValues) {
        for (CustomFontStyles fontStyle:CustomFontStyles.values()) {
            entries.add(Utils.getInternationalizedString(fontStyle.getTitle()));
            entryValues.add(String.valueOf(fontStyle.getResId()));
        }
    }

    private boolean isNotEmpty(Object newValue) {
        if(newValue.toString().trim().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setLanguageOptions(final Preference preference) {
        LanguagesFactory languagesFactory =
                new LanguagesFactory();
        GetAllLanguagesUseCase getAllLanguagesUseCase =
                languagesFactory.getGetLanguagesUseCase();
        getAllLanguagesUseCase.execute(new GetAllLanguagesUseCase.Callback() {
            @Override
            public void onSuccess(List<Language> languages) {
                ListPreference listPreference = (ListPreference) preference;
                if (languages.isEmpty()) {
                    listPreference.setEntries(R.array.languages_strings);
                    listPreference.setEntryValues(R.array.languages_codes);
                } else {
                    setLanguagesFromDB(listPreference, languages);
                }
            }
        });
    }

    private void setLanguagesFromDB(ListPreference listPreference, List<Language> languages) {
        CharSequence systemDefinedString =
                settingsActivity.getResources().getStringArray(
                        R.array.languages_strings)[0];
        CharSequence systemDefinedCode = settingsActivity.getResources().getStringArray(
                R.array.languages_codes)[0];
        CharSequence[] languagesStrings = new CharSequence[languages.size()+1];
        CharSequence[] languagesCodes = new CharSequence[languages.size()+1];
        languagesStrings[0] = systemDefinedString;
        languagesCodes[0] = systemDefinedCode;
        int languagesPosition = 1;
        for (int i = 0; i < languages.size(); i++) {
            languagesStrings[languagesPosition] = languages.get(i).getName();
            languagesCodes[languagesPosition] = languages.get(i).getCode();
            languagesPosition++;
        }
        listPreference.setEntries(languagesStrings);
        listPreference.setEntryValues(languagesCodes);
        listPreference.setSummary(
                getPositionOfCode(listPreference.getValue(), languagesCodes, languagesStrings));
    }

    private CharSequence getPositionOfCode(String value, CharSequence[] languagesCodes,
            CharSequence[] languagesNames) {
        if (languagesCodes != null && value != null) {
            for (int i = 0; i < languagesCodes.length; i++) {
                if (languagesCodes[i].equals(value)) {
                    return languagesNames[i];
                }
            }
        }
        return settingsActivity.getResources().getStringArray(
                R.array.languages_strings)[0];
    }
}
