package org.eyeseetea.malariacare.data.database.datasources;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAuthRepository;
import org.eyeseetea.malariacare.domain.entity.intent.Auth;

public class AuthDataSource implements IAuthRepository{

    Context context;

    public AuthDataSource(Context context){
        this.context = context;
    }

    @Override
    public Auth getAuth() {
        return getAuthFromPreferences();
    }

    @Override
    public void saveAuth(Auth auth) {
        saveAuthAsPreferences(auth);
    }

    @Override
    public void clearAuth() {
        clearAuthPreferences();
    }

    private void saveAuthAsPreferences(Auth auth) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(auth==null){
            editor.putString(context.getString(R.string.intent_credentials_user), null);
            editor.putString(context.getString(R.string.intent_credentials_password), null);
        }else {
            editor.putString(context.getString(R.string.intent_credentials_user), auth.getUserName());
            editor.putString(context.getString(R.string.intent_credentials_password), auth.getPassword());
        }
        editor.apply();
    }

    private Auth getAuthFromPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        String user = sharedPreferences.getString(
                context.getResources().getString(R.string.intent_credentials_user),
                null);
        String password = sharedPreferences.getString(
                context.getResources().getString(R.string.intent_credentials_password),
                null);
        if(user == null || password == null) {
            return null;
        }else {
            return new Auth(user, password);
        }
    }

    private void clearAuthPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.intent_credentials_user), null);
        editor.putString(context.getString(R.string.intent_credentials_password), null);

        editor.apply();
    }
}
