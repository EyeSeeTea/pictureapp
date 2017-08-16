package org.eyeseetea.malariacare.data.remote.ports;

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
import com.google.api.services.drive.model.FileList;

import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.boundary.ports.IFileDownloader;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.strategies.DashboardActivityStrategy;
import org.eyeseetea.sdk.data.DownloadMediaTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FileDownloader implements IFileDownloader {


    private GoogleCredential serviceCredential;
    private static final String[] SCOPES = {DriveScopes.DRIVE};
    private final File mFileDir;
    private final InputStream mPrivateJsonStream;
    private Drive drive;

    public FileDownloader(File fileDir, InputStream privateJsonStream) {
        mFileDir = required(fileDir, "File dir is required");
        mPrivateJsonStream = required(privateJsonStream,
                "Private Json stream with google play key is required");
    }

    public void init(String rootUid, final DashboardActivityStrategy.Callback mCallback){
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

        drive = new Drive.Builder(
                transport, jsonFactory, null)
                .setHttpRequestInitializer(serviceCredential)
                .build();
        List<Media> mediaFiles = new ArrayList<>();
        try {
            FileList folders = drive.files().list().setQ("\'"+ rootUid +"\' in parents").execute();
            for(com.google.api.services.drive.model.File file:folders.getFiles()){
                if(file.getMimeType().equals("application/vnd.google-apps.folder")){
                    if(file.getName().equals(ProgramDB.getProgram(PreferencesEReferral.getUserProgramId()).getName())) {
                        FileList fileList = drive.files().list().setQ(
                                "\'" + file.getId() + "\' in parents").execute();
                        mediaFiles = convertInMediaList(fileList);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(Media media:mediaFiles){
            mCallback.save(media);
        }
    }


    private static List<Media> convertInMediaList(FileList fileList) {
        List <Media> mediaList= new ArrayList<>();
        for(com.google.api.services.drive.model.File file:fileList.getFiles()){
            if(!file.getMimeType().equals("application/vnd.google-apps.folder")){
                Media.MediaType mediaType = Media.MediaType.VIDEO;
                if(file.getMimeType().contains("image")){
                    mediaType = Media.MediaType.PICTURE;
                }
                else if (file.getMimeType().contains("video")){
                    mediaType = Media.MediaType.VIDEO;
                }
                mediaList.add(new Media(file.getId(), mediaType));
            }

        }
        return mediaList;
    }

    @Override
    public void download(List<String> uids, final DashboardActivityStrategy.Callback mCallback) {
        if(drive==null){
            System.out.println("Drive is null, init() method must be called first");
        }
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
