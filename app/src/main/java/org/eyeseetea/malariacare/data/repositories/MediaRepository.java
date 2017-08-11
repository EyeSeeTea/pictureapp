package org.eyeseetea.malariacare.data.repositories;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.data.database.model.MediaDB;
import org.eyeseetea.malariacare.data.database.model.MediaDB_Table;
import org.eyeseetea.malariacare.data.mappers.MediaMapper;
import org.eyeseetea.malariacare.domain.boundary.repositories.IMediaRepository;
import org.eyeseetea.malariacare.domain.entity.Media;

import java.util.List;

public class MediaRepository implements IMediaRepository {

    public List<Media> getAll() {
        return MediaMapper.mapFromDbToDomain(getAllMediaDB());
    }

    private List<MediaDB> getAllMediaDB() {
        return new Select().
                from(MediaDB.class).
                orderBy(MediaDB_Table.id_media, true).
                queryList();
    }
}
