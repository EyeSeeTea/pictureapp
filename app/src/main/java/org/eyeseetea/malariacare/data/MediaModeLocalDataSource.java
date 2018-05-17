package org.eyeseetea.malariacare.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.IMediaModeRepository;
import org.eyeseetea.malariacare.domain.entity.Settings;


public class MediaModeLocalDataSource implements IMediaModeRepository {

    private final Context context;

    public MediaModeLocalDataSource(){
        context =PreferencesState.getInstance().getContext();
    }

    private void setMediaPreference(String listType) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getResources().getString(R.string.media_list_style_preference), listType);
        editor.commit();
    }

    private String getMediaPreference() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        return sharedPreferences.getString(
                context.getResources().getString(R.string.media_list_style_preference),
                "");
    }

    @Override
    public Settings.MediaListMode getMediaListMode() {
        Settings.MediaListMode mediaListMode = null;
        if(getMediaPreference().equals(Settings.MediaListMode.GRID.toString())){
            mediaListMode = Settings.MediaListMode.GRID;
        }else if(getMediaPreference().equals(Settings.MediaListMode.LIST.toString())){
            mediaListMode = Settings.MediaListMode.LIST;
        }
        return mediaListMode;
    }

    @Override
    public void saveMediaListMode(Settings.MediaListMode mediaListMode) {
        setMediaPreference(mediaListMode.toString());
    }
}
