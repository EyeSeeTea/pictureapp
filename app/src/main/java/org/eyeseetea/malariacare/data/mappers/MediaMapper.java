package org.eyeseetea.malariacare.data.mappers;

import org.eyeseetea.malariacare.data.database.model.MediaDB;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.utils.Constants;

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
        return new Media(mediaDB.getFilename(), mediaDB.getResourceUrl(),
                getMediaType(mediaDB.getMediaType()),
                Media.getSizeInMB(mediaDB.getFilename()));
    }

    private static Media.MediaType getMediaType(int mediaType) {
        if (mediaType == Constants.MEDIA_TYPE_IMAGE) {
            return Media.MediaType.PICTURE;
        } else if (mediaType == Constants.MEDIA_TYPE_VIDEO) {
            return Media.MediaType.VIDEO;
        }
        return null;
    }
}