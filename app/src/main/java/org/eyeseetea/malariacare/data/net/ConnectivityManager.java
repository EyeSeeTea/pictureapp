package org.eyeseetea.malariacare.data.net;

import android.content.Context;
import android.net.NetworkInfo;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;

public class ConnectivityManager implements IConnectivityManager{


    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    @Override
    public boolean isDeviceOnline() {
        android.net.ConnectivityManager connMgr =
                (android.net.ConnectivityManager) DashboardActivity.dashboardActivity.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(android.net.ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }

    @Override
    public ConnectivityType getConnectivityType() {
        android.net.ConnectivityManager connMgr =
                (android.net.ConnectivityManager) DashboardActivity.dashboardActivity
                        .getSystemService(
                                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(android.net.ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            return ConnectivityType.WIFI;
        }
        networkInfo = connMgr.getNetworkInfo(android.net.ConnectivityManager.TYPE_MOBILE);
        if (networkInfo.isConnected()) {
            return ConnectivityType.MOBILE;
        }
        return ConnectivityType.NONE;
    }
}
