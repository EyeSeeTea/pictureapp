package org.eyeseetea.malariacare.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;

public class BanOrgUnitExecutor {
    //TODO: this class exists just for realize network operations
    //about ban org units out of ui thread, when we refactor threads this class
    // should be removed it

    public interface banOrgUnitCallback {
        void onSuccess();

        void onError();
    }

    public interface isOrgUnitBannedCallback {
        void onSuccess(Boolean isBanned);

        void onError();

        void onNetworkError();
    }

    public void banOrgUnit(banOrgUnitCallback banOrgUnitCallback) {
        new BanOrgUnitAsync(banOrgUnitCallback).execute();
    }

    public void isOrgUnitBanned(isOrgUnitBannedCallback isOrgUnitBannedCallback) {
        //Check orgUnit state in server
        if (isNetworkAvailable()) {
            new CheckBanOrgUnitAsync(isOrgUnitBannedCallback).execute();
        } else {
            isOrgUnitBannedCallback.onNetworkError();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) PreferencesState.getInstance().getContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}

class BanOrgUnitAsync extends AsyncTask<Void, Void, Boolean> {

    BanOrgUnitExecutor.banOrgUnitCallback mBanOrgUnitCallback;

    public BanOrgUnitAsync(BanOrgUnitExecutor.banOrgUnitCallback banOrgUnitCallback) {
        mBanOrgUnitCallback = banOrgUnitCallback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String url = ServerAPIController.getServerUrl();
        String orgUnitNameOrCode = ServerAPIController.getOrgUnit();

        try {
            if (!orgUnitNameOrCode.isEmpty()) {
                ServerAPIController.banOrg(url, orgUnitNameOrCode);
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            mBanOrgUnitCallback.onSuccess();
        } else {
            mBanOrgUnitCallback.onError();
        }
    }
}

class CheckBanOrgUnitAsync extends AsyncTask<Void, Void, Boolean> {

    BanOrgUnitExecutor.isOrgUnitBannedCallback mBanOrgUnitCallback;

    public CheckBanOrgUnitAsync(BanOrgUnitExecutor.isOrgUnitBannedCallback banOrgUnitCallback) {
        mBanOrgUnitCallback = banOrgUnitCallback;
    }

    protected Boolean doInBackground(Void... param) {
        String url = ServerAPIController.getServerUrl();
        String orgUnitNameOrCode = ServerAPIController.getOrgUnit();

        if (orgUnitNameOrCode.isEmpty()) {
            return false;
        }

        try {
            return !ServerAPIController.isOrgUnitOpen(url, orgUnitNameOrCode);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mBanOrgUnitCallback.onSuccess(result);
    }


}
