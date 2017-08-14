package org.eyeseetea.malariacare.data.remote.strategies;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.domain.entity.Media;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DownloaderMediaStrategy {

    public static void init(Drive mService, MediaRepository mMediaRepository,  String rootUid){
        List<Media> mediaFiles = new ArrayList<>();
        try {
            FileList folders = mService.files().list().setQ("\'"+ rootUid +"\' in parents").execute();
            for(File file:folders.getFiles()){
                if(file.getMimeType().equals("application/vnd.google-apps.folder")){
                    if(file.getName().equals(ProgramDB.getProgram(PreferencesEReferral.getUserProgramId()).getName())) {
                        FileList fileList = mService.files().list().setQ(
                                "\'" + file.getId() + "\' in parents").execute();
                        mediaFiles = convertInMediaList(fileList);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(Media media:mediaFiles){
            mMediaRepository.updateNotDownloadedMedia(media);
        }
    }


    private static List<Media> convertInMediaList(FileList fileList) {
        List <Media> mediaList= new ArrayList<>();
        for(File file:fileList.getFiles()){
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
}
