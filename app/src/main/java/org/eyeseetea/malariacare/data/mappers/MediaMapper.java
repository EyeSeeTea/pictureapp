package org.eyeseetea.malariacare.data.mappers;

import org.eyeseetea.malariacare.data.database.model.MediaDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.utils.Constants;
import org.eyeseetea.sdk.common.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class MediaMapper {

    public static List<Media> mapFromDbToDomain(List<MediaDB> mediaDBs) {
        List<Media> medias = new ArrayList<>();
        for (MediaDB mediaDB : mediaDBs) {
            medias.add(mapFromDbToDomain(mediaDB));
        }
        return medias;
    }

    public static Media mapFromDbToDomain(MediaDB mediaDB) {
        return new Media(mediaDB.getId_media(), Media.getFilenameFromPath(mediaDB.getFilename()),
                mediaDB.getFilename(), mediaDB.getResourceUrl(),
                getMediaType(mediaDB.getMediaType()),
                getSizeInMB(mediaDB.getFilename()), mediaDB.getProgram());
    }

    private static Media.MediaType getMediaType(int mediaType) {
        Media.MediaType type = null;
        switch (mediaType) {
            case Constants.MEDIA_TYPE_IMAGE: {
                type = Media.MediaType.PICTURE;
                break;
            }
            case Constants.MEDIA_TYPE_VIDEO: {
                type = Media.MediaType.VIDEO;
                break;
            }
            case Constants.MEDIA_TYPE_UNKNOWN: {
                type = Media.MediaType.UNKNOWN;
                break;
            }
        }
        return type;
    }

    private static int getConstant(Media.MediaType mediaType) {
        int type = Constants.MEDIA_TYPE_UNKNOWN;
        switch (mediaType) {
            case PICTURE:
                type = Constants.MEDIA_TYPE_IMAGE;
                break;
            case VIDEO:
                type = Constants.MEDIA_TYPE_VIDEO;
                break;
            case UNKNOWN:
                type = Constants.MEDIA_TYPE_UNKNOWN;
                break;
        }
        return type;
    }

    public static String getSizeInMB(String filename) {
        String size = "NaN";
        try{
            FileUtils.getSizeInMB(filename, PreferencesState.getInstance().getContext());
        }catch (NullPointerException e){
            e.printStackTrace();
            //The file not exist
        }
        return size;
    }

    public static MediaDB mapFromDomainToDb(Media media) {
        return new MediaDB(getConstant(media.getType()), media.getResourceUrl(),
                media.getResourcePath(), media.getProgram());
    }
}
