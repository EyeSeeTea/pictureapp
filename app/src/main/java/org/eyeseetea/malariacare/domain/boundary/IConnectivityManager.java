package org.eyeseetea.malariacare.domain.boundary;

import org.eyeseetea.malariacare.data.net.ConnectivityType;

public interface IConnectivityManager {
    boolean isDeviceOnline();

    ConnectivityType getConnectivityType();
}
