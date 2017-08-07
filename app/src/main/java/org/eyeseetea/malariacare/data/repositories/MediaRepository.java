package org.eyeseetea.malariacare.data.repositories;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.data.database.model.MediaDB;
import org.eyeseetea.malariacare.data.database.model.MediaDB_Table;
import org.eyeseetea.malariacare.data.database.model.QuestionDB;
import org.eyeseetea.malariacare.domain.entity.Media;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ignac on 07/08/2017.
 */

public class MediaRepository {

    public static List<MediaDB> getAllNotInLocal() {
        return new Select().
                from(MediaDB.class).
                where(MediaDB_Table.filename.isNull()).
                and(MediaDB_Table.resource_url.isNotNull()).
                orderBy(MediaDB_Table.id_media, true).
                queryList();
    }

    public static List<MediaDB> getAllInLocal() {
        return new Select().
                from(MediaDB.class).
                where(MediaDB_Table.filename.isNotNull()).
                and(MediaDB_Table.resource_url.isNotNull()).
                orderBy(MediaDB_Table.id_media, true).
                queryList();
    }

    public static List<MediaDB> getAllMediaDB() {
        return new Select().
                from(MediaDB.class).
                orderBy(MediaDB_Table.id_media, true).
                queryList();
    }

    public static List<Media> getAllMedia() {
        return fromModel(getAllMediaDB());
    }

    public static List<MediaDB> findByQuestion(QuestionDB question) {
        if (question == null) {
            return new ArrayList<>();
        }

        return new Select().
                from(MediaDB.class).
                where(MediaDB_Table.id_question_fk.eq(question.getId_question())).
                orderBy(MediaDB_Table.id_media, true).
                queryList();
    }


    /**
     * Returns a media that holds a reference to the same resource with an already downloaded copy
     * of the file.
     */
    public MediaDB findLocalCopy(long idMedia, String resourceUrl) {
        return new Select().from(MediaDB.class)
                .where(MediaDB_Table.filename.isNotNull())
                .and(MediaDB_Table.id_media.isNot(idMedia))
                .and(MediaDB_Table.resource_url.is(resourceUrl))
                .querySingle();
    }

    public static List<Media> fromModel(List<MediaDB> mediaDBs){
        List<Media> medias = new ArrayList<>();
        for(MediaDB mediaDB:mediaDBs){
            medias.add(fromModel(mediaDB));
        }
        return medias;
    }

    public static Media fromModel(MediaDB mediaDB){
        return new Media(mediaDB.getFilename(), mediaDB.getResourceUrl(), mediaDB.getMediaType(),
                Media.getSizeInMB(mediaDB.getFilename()));
    }
}
