package org.eyeseetea.malariacare.network.factory;

import android.content.Context;
import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.net.ConnectivityManager;
import org.eyeseetea.malariacare.domain.boundary.IConnectivityManager;


public class NetworkManagerFactory {

    private static IConnectivityManager sConnectivityManager;

    @NonNull
    public static IConnectivityManager getConnectivityManager(final Context context) {
        if (sConnectivityManager == null) {
            sConnectivityManager = new IConnectivityManager() {
                @Override
                public boolean isDeviceOnline() {
                    return new ConnectivityManager().isDeviceOnline();
                }
            };
        }

        return sConnectivityManager;
    }
}
