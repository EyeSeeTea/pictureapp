package org.eyeseetea.malariacare.data.database.datasources;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.IDataSourceCallback;
import org.eyeseetea.malariacare.data.database.model.CountryVersionDB;
import org.eyeseetea.malariacare.data.sync.importer.MetadataUpdater;
import org.eyeseetea.malariacare.domain.boundary.repositories.IAppInfoRepository;
import org.eyeseetea.malariacare.domain.entity.AppInfo;

import java.io.IOException;
import java.util.Date;


public class AppInfoDataSource implements IAppInfoRepository {

    private static final String TAG = "AppInfoDataSource";
    private Context context;

    public AppInfoDataSource(Context context) {
        this.context = context;
    }

    @Override
    public AppInfo getAppInfo() {
        return new AppInfo(getMetadataVersion(), getConfigFileVersion(), getAppVersion(),
                getUpdateMetadataDate(), getLastPushDate());
    }


    @Override
    public void getAppInfo(IDataSourceCallback<AppInfo> callback) {
        callback.onSuccess(
                new AppInfo(getMetadataVersion(), getConfigFileVersion(), getAppVersion(),
                        getUpdateMetadataDate(), getLastPushDate()));
    }

    @Override
    public void saveAppInfo(AppInfo appInfo) {
        saveMetadataUpdateDate(appInfo.getUpdateMetadataDate());
    }

    private String getMetadataVersion() {
        try {
            return String.valueOf(MetadataUpdater.getPhoneCSVersion());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error getting metadata version: " + e.getMessage());
        }
        return null;
    }
    //TODO Replace  getMetadataVersion with this implentation
    public String getConfigFileVersion() {
           return String.valueOf(CountryVersionDB.getMetadataVersion());
    }

    private String getAppVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Error getting app version" + e.getMessage());
        }
        return String.valueOf(pInfo.versionCode);
    }

    private void saveMetadataUpdateDate(Date updateMetadataName) {
        if (updateMetadataName != null) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                    context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(context.getResources().getString(R.string.metadata_update_date),
                    updateMetadataName.getTime());
            editor.commit();
        }
    }


    private Date getUpdateMetadataDate() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        long timeMillis = sharedPreferences.getLong(
                context.getResources().getString(R.string.metadata_update_date), 0);
        if (timeMillis == 0) {
            return null;
        }
        return new Date(timeMillis);
    }


    private Date getLastPushDate() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        long timeMillis = sharedPreferences.getLong(
                context.getResources().getString(R.string.last_push_date), 0);
        return new Date(timeMillis);
    }

    private void saveLastPushDate(Date lastPushDate){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(
                context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(context.getResources().getString(R.string.last_push_date),
                lastPushDate.getTime());
        editor.commit();
    }
}