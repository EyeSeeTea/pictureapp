package org.eyeseetea.malariacare.data.remote.drive;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth
        .GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import org.eyeseetea.malariacare.DashboardActivity;
import org.eyeseetea.malariacare.data.remote.strategies.DownloaderMediaStrategy;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.entity.Media;

import java.io.FileOutputStream;
import java.util.List;

/**
 * AsyncTask that downloads media files
 * Created by arrizabalaga on 28/05/16.
 */
class DownloadMediaTask extends AsyncTask<Void, Void, Integer> {


    //TODO  Change by the correct uid
    private static final String rootUid = "0B8oszoX2-DdHOFZwVmRyTWc1X3c";
    private static final String QUICKTIME_NON_SUPPORTED_FORMAT = "mov";
    private final String TAG = "DownloadMediaTask";
    private Drive mService = null;
    private Exception mLastError = null;
    private MediaRepository mMediaRepository;
    private Context mContext;
    private DriveRestController.CallBack mCallBack;

    /**
     * Builds a task that requires service credentials
     */
    public DownloadMediaTask(GoogleCredential credential, MediaRepository mediaRepository,
            Context context, DriveRestController.CallBack callBack) {
        mCallBack = callBack;
        this.mContext = context;
        this.mMediaRepository = mediaRepository;
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        this.mService = new Drive.Builder(
                transport, jsonFactory, null)
                .setHttpRequestInitializer(credential)
                .build();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        Log.d(TAG, String.format("DownloadMediaTask starts"));

        DownloaderMediaStrategy.init(mService, mMediaRepository, rootUid);
        //Check elements to download
        List<Media> mediaList = mMediaRepository.getAllNotDownloaded();

        //Nothing to sync -> 0
        if (mediaList == null || mediaList.isEmpty()) {
            Log.d(TAG, String.format("DownloadMediaTask nothing to sync"));
            return 0;
        }

        //Try to download every non local media
        int numSyncedFiles = 0;
        for (Media media : mediaList) {
            Exception ex = sync(media);
            //No exception -> inc & next
            if (ex == null) {
                Log.d(TAG, String.format("DownloadMediaTask file synced"));
                numSyncedFiles++;
            } else if (ex instanceof UserRecoverableAuthIOException) {
                Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_LONG).show();
                //First time auth confirm from user lets stop and try again after confirming
                break;
            }

            //Any other exception -> Non existent resource, just move forward
        }
        return numSyncedFiles;
    }

    @Override
    protected void onPostExecute(Integer numSyncedFiles) {
        if (numSyncedFiles > 0) {
            Toast.makeText(mContext, String.format("%d files synced", numSyncedFiles),
                    Toast.LENGTH_LONG).show();
            mCallBack.onSuccess();
        }
    }

    @Override
    protected void onCancelled() {

        //Need to complete credentials (ack from user first time)
        if (mLastError instanceof UserRecoverableAuthIOException) {
            DashboardActivity.dashboardActivity.startActivityForResult(
                    ((UserRecoverableAuthIOException) mLastError).getIntent(),
                    DriveRestController.REQUEST_AUTHORIZATION);
            return;
        }

        //Real connection google error
        if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
            DriveRestController.getInstance().showGooglePlayServicesAvailabilityErrorDialog(
                    ((GooglePlayServicesAvailabilityIOException) mLastError)
                            .getConnectionStatusCode());
            return;
        }

        //Other error
        Log.e(TAG, "onCancelled: " + mLastError == null ? "" : mLastError.getMessage());
    }

    private Exception sync(Media media) {

        //Try to reuse a local copy if another media references same url
        Media localCopy = mMediaRepository.findLocalCopy(media.getId(), media.getResourceUrl());
        if (localCopy != null) {
            return syncFromLocal(media, localCopy);
        }

        //Download from google drive
        try {
            //download file
            String absolutePath = downloadFile(media.getResourceUrl());
            //save local path to use later
            media.setResourcePath(absolutePath);
            mMediaRepository.updateModel(media);
            Log.d(TAG, "\tsaved in " + absolutePath);
            return null;
        } catch (IllegalStateException ex) {
            Log.e(TAG, "downloadFile error (file extension not supported)" + ex.getMessage());
            //Delete non supported formats
            mMediaRepository.deleteModel(media);
            return ex;
        } catch (Exception ex) {
            Log.e(TAG, "downloadFile error: " + ex.getMessage());

            //A regular exception must be annotated for processing later onCancelled
            mLastError = ex;
            //Cancels current async task (ask for permissions first time might be)
            cancel(true);
            return ex;
        }
    }

    /**
     * Updates media with the same path of another media referencing same resource in Drive
     */
    private Exception syncFromLocal(Media media, Media localCopy) {
        media.setResourceUrl(localCopy.getResourceUrl());
        mMediaRepository.updateResourcePath(media);
        return null;
    }


    private String downloadFile(String resourceId) throws Exception {
        Log.d(TAG, String.format("Downloading resource: %s ...", resourceId));

        Drive.Files.Get getFile = mService.files().get(resourceId);
        File fileDrive = getFile.execute();
        Log.d(TAG, "\tfilename: " + fileDrive.getName());

        if (fileDrive.getName().endsWith(QUICKTIME_NON_SUPPORTED_FORMAT)) {
            throw new IllegalStateException(
                    String.format("%s format not supported in Android", fileDrive.getName()));
        }

        java.io.File localFile = new java.io.File(DashboardActivity.dashboardActivity.getFilesDir(),
                fileDrive.getName());
        FileOutputStream fileOutputStream = new FileOutputStream(localFile);
        getFile.executeMediaAndDownloadTo(fileOutputStream);
        fileOutputStream.close();
        return localFile.getAbsolutePath();
    }
}
