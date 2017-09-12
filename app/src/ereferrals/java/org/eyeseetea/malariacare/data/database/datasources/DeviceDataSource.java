package org.eyeseetea.malariacare.data.database.datasources;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.repositories.IDeviceRepository;
import org.eyeseetea.malariacare.domain.entity.Device;


public class DeviceDataSource implements IDeviceRepository {
    @Override
    public Device getDevice() {
        return new Device(getDevicePhone(), getDeviceIMEI(), getAndroidVersion());
    }

    private String getDeviceIMEI() {
        Context context = PreferencesState.getInstance().getContext();
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        return tMgr.getDeviceId();
    }

    private String getDevicePhone() {
        Context context = PreferencesState.getInstance().getContext();
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        return tMgr.getLine1Number();
    }

    private String getAndroidVersion() {
        return Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
    }

}
