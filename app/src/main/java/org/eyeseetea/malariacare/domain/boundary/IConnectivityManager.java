package org.eyeseetea.malariacare.domain.boundary;

public interface IConnectivityManager {
    boolean isDeviceOnline(boolean canDownloadWith3G);
}
