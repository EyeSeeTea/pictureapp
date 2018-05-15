package org.eyeseetea.malariacare.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.IStylePreferencesRepository;


public class StylePreferenceLocalDataSource implements IStylePreferencesRepository {
    private final static ListStyle DEFAULT_LIST_STYLE = ListStyle.GRID;

    private final Context context;

    public StylePreferenceLocalDataSource(){
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
                DEFAULT_LIST_STYLE.toString());
    }

    @Override
    public ListStyle getListStyle() {
        IStylePreferencesRepository.ListStyle listStyle = null;
        if(getMediaPreference().equals(IStylePreferencesRepository.ListStyle.GRID.toString())){
            listStyle = IStylePreferencesRepository.ListStyle.GRID;
        }else if(getMediaPreference().equals(IStylePreferencesRepository.ListStyle.LIST.toString())){
            listStyle = IStylePreferencesRepository.ListStyle.LIST;
        }
        return listStyle;
    }

    @Override
    public void saveListStyle(ListStyle listStyle) {
        setMediaPreference(listStyle.toString());
    }
}
