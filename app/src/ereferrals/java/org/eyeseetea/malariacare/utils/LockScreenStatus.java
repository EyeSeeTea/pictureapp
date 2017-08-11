package org.eyeseetea.malariacare.utils;

import android.app.KeyguardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class LockScreenStatus {

    public static String TAG = "LockScreenStatus";

    /**
     * @return true if pattern set, false if not (or if an issue when checking)
     */
    public static boolean isPatternSet(Context context) {
        ContentResolver cr = context.getContentResolver();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(
                    Context.KEYGUARD_SERVICE);
            if (keyguardManager.isDeviceSecure()) {
                return true;
            }
        } else {
            try {
                int lockPatternEnable = Settings.Secure.getInt(cr,
                        Settings.Secure.LOCK_PATTERN_ENABLED);
                return lockPatternEnable == 1;
            } catch (Settings.SettingNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return false;
    }
}
