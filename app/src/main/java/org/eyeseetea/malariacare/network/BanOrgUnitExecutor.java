package org.eyeseetea.malariacare.network;

import android.os.AsyncTask;

import org.eyeseetea.malariacare.domain.exception.ApiCallException;

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
    }

    public void banOrgUnit(banOrgUnitCallback banOrgUnitCallback) {
        new BanOrgUnitAsync(banOrgUnitCallback).execute();
    }

    public void isOrgUnitBanned(isOrgUnitBannedCallback isOrgUnitBannedCallback) {
        //Check orgUnit state in server

        new CheckBanOrgUnitAsync(isOrgUnitBannedCallback).execute();
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

        if (!orgUnitNameOrCode.isEmpty()) {
            return ServerAPIController.banOrg(url, orgUnitNameOrCode);
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result != null && result) {
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
        Boolean isBanned;
        try {
            isBanned = !ServerAPIController.isOrgUnitOpen(url, orgUnitNameOrCode);
        } catch (ApiCallException e) {
            isBanned = null;
        }
        return isBanned;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mBanOrgUnitCallback.onSuccess(result);
    }
}