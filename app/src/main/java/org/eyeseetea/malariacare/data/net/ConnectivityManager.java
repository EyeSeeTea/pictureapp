package org.eyeseetea.malariacare.data.net;

import android.content.Context;
import android.net.NetworkInfo;

import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;

public class ConnectivityManager implements IConnectivityManager{

    private Context mContext;

    public ConnectivityManager(Context context) {
        this.mContext = context;
    }
    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    @Override
    public boolean isDeviceOnline() {
        ConnectivityType connectivityType = getConnectivityType();

        return connectivityType == ConnectivityType.MOBILE ||
                connectivityType == ConnectivityType.WIFI;
    }

    @Override
    public ConnectivityType getConnectivityType() {
        android.net.ConnectivityManager connMgr =
                (android.net.ConnectivityManager) mContext.getSystemService(
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
