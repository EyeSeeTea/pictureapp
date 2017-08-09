package org.eyeseetea.malariacare.data.repositories;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.data.database.model.MediaDB;
import org.eyeseetea.malariacare.data.database.model.MediaDB_Table;
import org.eyeseetea.malariacare.domain.boundary.repositories.IMediaRepository;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MediaRepository implements IMediaRepository {

    public List<Media> getAll() {
        return fromModel(getAllMediaDB());
    }

    private List<MediaDB> getAllMediaDB() {
        return new Select().
                from(MediaDB.class).
                orderBy(MediaDB_Table.id_media, true).
                queryList();
    }

    private List<Media> fromModel(List<MediaDB> mediaDBs) {
        List<Media> medias = new ArrayList<>();
        for (MediaDB mediaDB : mediaDBs) {
            medias.add(fromModel(mediaDB));
        }
        return medias;
    }

    private Media fromModel(MediaDB mediaDB) {
        return new Media(mediaDB.getFilename(), mediaDB.getResourceUrl(),
                getMediaType(mediaDB.getMediaType()),
                Media.getSizeInMB(mediaDB.getFilename()));
    }

    private Media.MediaType getMediaType(int mediaType) {
        if (mediaType == Constants.MEDIA_TYPE_IMAGE) {
            return Media.MediaType.PICTURE;
        } else if (mediaType == Constants.MEDIA_TYPE_VIDEO) {
            return Media.MediaType.VIDEO;
        }
        return null;
    }
}
