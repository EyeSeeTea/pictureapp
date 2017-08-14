package org.eyeseetea.malariacare.data.remote.drive;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.drive.DriveScopes;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.MediaDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class DriveRestController {
    public interface CallBack{
        void onSuccess();
    }

    private static final String TAG = "DriveRestController";
    private static DriveRestController instance;

    private GoogleCredential serviceCredential;
    private static final String[] SCOPES = {DriveScopes.DRIVE};

    static final int REQUEST_AUTHORIZATION = 101;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 102;

    private Context mContext;
    private MediaRepository mMediaRepository;
    private CallBack mCallBack;

    DriveRestController() {

    }

    public static DriveRestController getInstance() {
        if (instance == null) {
            instance = new DriveRestController();
        }
        return instance;
    }

    public void init(MediaRepository mediaRepository, Context context, CallBack callBack) {
        Log.d(TAG, "Init drive credential");
        mContext = context;
        mMediaRepository = mediaRepository;
        initServiceAccountCredential();
        mCallBack = callBack;
    }

    private void initServiceAccountCredential() {
        try {
            InputStream privateJsonStream = mContext.getResources().openRawResource(
                    R.raw.driveserviceprivatekey);
            serviceCredential = GoogleCredential.fromStream(privateJsonStream).createScoped(
                    Arrays.asList(SCOPES));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    public void syncMedia() {

        if (!isDeviceOnline()) {
            Log.w(TAG, "No wifi connection available. Media will not be synced");
            return;
        }

        if (!isGooglePlayServicesAvailable()) {
            if (!isGooglePlayAppAvailable()) {
                Log.w(TAG, "No GooglePlay Available . acquire play services will not possible");
                return;
            }
            acquireGooglePlayServices();
            return;
        }

        new DownloadMediaTask(serviceCredential, mMediaRepository, mContext, mCallBack).execute();
    }

    private boolean isGooglePlayAppAvailable() {
        try {
            PreferencesState.getInstance().getContext().getPackageManager()
                    .getPackageInfo(GooglePlayServicesUtil.GOOGLE_PLAY_STORE_PACKAGE, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Called when an activity launched here (specifically, AccountPicker
     * and authorization) exits, giving you the requestCode you started it with,
     * the resultCode it returned, and any additional data from it.
     *
     * @param requestCode code indicating which activity result is incoming.
     * @param resultCode  code indicating the result of the incoming
     *                    activity result.
     * @param data        Intent (containing result data) returned by incoming
     *                    activity result.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != Activity.RESULT_OK) {
                    Toast.makeText(mContext, mContext.getString(R.string.google_play_required),
                            Toast.LENGTH_LONG);
                } else {
                    syncMedia();
                }
                break;
        }
    }

    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) DashboardActivity.dashboardActivity.getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }

    /**
     * Check that Google Play services APK is installed and up to date.
     *
     * @return true if Google Play Services is available and up to
     * date on this device; false otherwise.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(DashboardActivity.dashboardActivity);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(mContext);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }


    /**
     * Display an error dialog showing that Google Play Services is missing
     * or out of date.
     *
     * @param connectionStatusCode code describing the presence (or lack of)
     *                             Google Play Services on this device.
     */
    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                DashboardActivity.dashboardActivity,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }
}
