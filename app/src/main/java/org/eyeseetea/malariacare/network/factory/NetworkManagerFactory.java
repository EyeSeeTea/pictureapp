package org.eyeseetea.malariacare.network.factory;

import android.content.Context;
import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.net.ConnectivityType;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;
import org.eyeseetea.malariacare.utils.ConnectivityStatus;

public class NetworkManagerFactory {

    private static IConnectivityManager sConnectivityManager;

    @NonNull
    public static IConnectivityManager getConnectivityManager(final Context context) {
        if (sConnectivityManager == null) {
            sConnectivityManager = new IConnectivityManager() {

                @Override
                public ConnectivityType getConnectivityType() {
                    return null;
                }

                @Override
                public boolean isDeviceOnline() {
                    return ConnectivityStatus.isConnected(context);
                }
            };
        }

        return sConnectivityManager;
    }
}
