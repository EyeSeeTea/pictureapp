package org.eyeseetea.malariacare.data.io;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import android.content.pm.PackageManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.boundary.io.IFileDownloader;
import org.eyeseetea.malariacare.strategies.DashboardActivityStrategy;
import org.eyeseetea.sdk.data.DownloadMediaTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FileDownloader implements IFileDownloader {


    private GoogleCredential serviceCredential;
    private static final String[] SCOPES = {DriveScopes.DRIVE};
    private final File mFileDir;
    private final InputStream mPrivateJsonStream;

    public FileDownloader(File fileDir, InputStream privateJsonStream) {
        mFileDir = required(fileDir, "File dir is required");
        mPrivateJsonStream = required(privateJsonStream,
                "Private Json stream with google play key is required");
    }

    @Override
    public void download(List<String> uids, final DashboardActivityStrategy.Callback mCallback) {
        initServiceAccountCredential();

        if (!isGooglePlayServicesAvailable()) {
            if (!isGooglePlayAppAvailable()) {
                return;
            }
            mCallback.acquireGooglePlayServices();
            return;
        }

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        //mMediaRepository

        Drive drive = new Drive.Builder(
                transport, jsonFactory, null)
                .setHttpRequestInitializer(serviceCredential)
                .build();
        new DownloadMediaTask(mFileDir, drive, uids,
                new DownloadMediaTask.Callback() {
                    @Override
                    public void onError(Exception error) {
                        if (error instanceof UserRecoverableAuthIOException) {
                            mCallback.showToast(error.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(Exception mLastError) {
                        mCallback.onCancel(mLastError);
                        //Other error
                        System.out.println("onCancelled: " + mLastError == null ? ""
                                : mLastError.getMessage());
                    }

                    @Override
                    public void onSuccess(HashMap<String, String> syncedFiles) {
                        mCallback.onSuccess(syncedFiles);
                    }

                    @Override
                    public void removeIllegalFile(String uid) {
                        mCallback.onRemove(uid);
                    }
                }).execute();
    }

    private void initServiceAccountCredential() {
        try {
            serviceCredential = GoogleCredential.fromStream(mPrivateJsonStream).createScoped(
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
            System.out.println("No GooglePlay Available . acquire play services will not possible");
            return false;
        }
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
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(
                PreferencesState.getInstance().getContext());
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }
}
