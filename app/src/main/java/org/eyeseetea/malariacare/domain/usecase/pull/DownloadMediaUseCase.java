package org.eyeseetea.malariacare.domain.usecase.pull;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.remote.strategies.DownloaderMediaStrategy;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.boundary.executors.IAsyncExecutor;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.domain.usecase.UseCase;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DownloadMediaUseCase  implements UseCase {

    public interface Callback{
        void onUpdatedItems();
    }

    //TODO  Change by the correct uid
    private static final String rootUid = "0B8oszoX2-DdHOFZwVmRyTWc1X3c";
    private static final String TAG = "DownloadMediaUseCase";

    private GoogleCredential serviceCredential;
    private static final String[] SCOPES = {DriveScopes.DRIVE};

    static final int REQUEST_AUTHORIZATION = 101;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 102;
    private Context mContext;
    private IAsyncExecutor mAsyncExecutor;
    private MediaRepository mMediaRepository;
    private Callback mCallback;

    public DownloadMediaUseCase(
            IAsyncExecutor asyncExecutor,
            MediaRepository mediaRepository,
            Context context, Callback callback) {
        mAsyncExecutor = asyncExecutor;
        mMediaRepository = mediaRepository;
        mContext = context;
        initServiceAccountCredential();
    }

    public void execute(Context context) {
        mContext = context;
        mAsyncExecutor.run(this);
    }

    /**
     * Attempt to call the API, after verifying that all the preconditions are
     * satisfied. The preconditions are: Google Play Services installed, an
     * account was selected and the device currently has online access. If any
     * of the preconditions are not satisfied, the app will prompt the user as
     * appropriate.
     */
    @Override
    public void run() {

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

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        //mMediaRepository

        Drive mService = new Drive.Builder(
                transport, jsonFactory, null)
                .setHttpRequestInitializer(serviceCredential)
                .build();

        //Download the media list from drive folder
        DownloaderMediaStrategy.init(mService, mMediaRepository, rootUid);

        final List<Media> medias = mMediaRepository.getAllNotDownloaded();
        List<String> uids = new ArrayList<>();
        for(Media media:medias){
            if(!uids.contains(media.getResourceUrl())) {
                uids.add(media.getResourceUrl());
            }
        }

        if (uids != null && !uids.isEmpty()) {
            new org.eyeseetea.sdk.data.DownloadMediaTask(mContext.getFilesDir(), mService , uids,
                    new org.eyeseetea.sdk.data.DownloadMediaTask.Callback() {
                        @Override
                        public void onError(Exception error) {
                            if (error instanceof UserRecoverableAuthIOException) {
                                showMessage(error.getMessage());
                                //First time auth confirm from user lets stop and try again after confirming
                            }
                        }

                        @Override
                        public void onCancelled(Exception mLastError) {
                            //Need to complete credentials (ack from user first time)
                            if (mLastError instanceof UserRecoverableAuthIOException) {
                                DashboardActivity.dashboardActivity.startActivityForResult(
                                        ((UserRecoverableAuthIOException) mLastError).getIntent(),
                                        REQUEST_AUTHORIZATION);
                                return;
                            }

                            //Real connection google error
                            if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                                showGooglePlayServicesAvailabilityErrorDialog(
                                        ((GooglePlayServicesAvailabilityIOException) mLastError)
                                                .getConnectionStatusCode());
                                return;
                            }

                            //Other error
                            Log.e(TAG, "onCancelled: " + mLastError == null ? "" : mLastError.getMessage());
                        }

                        @Override
                        public void onSuccess(HashMap<String, String> syncedFiles) {
                            int correctSyncedFiles=0;

                            //Try to reuse a local copy if another media references same url
                            if(syncedFiles.size()>0){
                                for(Media media:medias){
                                    String absolutePath = syncedFiles.get(media.getResourceUrl());
                                    List<Media> allMediaWithSameResource = mMediaRepository.getAllMediaByResourceUid(media.getResourceUrl());
                                    for(Media localMedia:allMediaWithSameResource) {
                                        Log.d(TAG, localMedia.toString()+ "\tsaved in " + absolutePath);
                                        localMedia.setResourcePath(absolutePath);
                                        mMediaRepository.updateModel(localMedia);
                                    }
                                }

                            }
                            if(correctSyncedFiles>0) {
                                showMessage(String.format("%d files synced", syncedFiles.size()));
                                mCallback.onUpdatedItems();
                            }
                        }

                        @Override
                        public void removeIllegalFile(String uid) {
                            Log.e(TAG, "downloadFile error (file extension not supported)" + uid);
                            mMediaRepository.deleteModel(mMediaRepository.findByUid(uid));
                        }
                    }).execute();
        }
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



    private void showMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
}
