package org.eyeseetea.malariacare.data.repositories;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.data.database.model.MediaDB;
import org.eyeseetea.malariacare.data.database.model.MediaDB_Table;
import org.eyeseetea.malariacare.data.mappers.MediaMapper;
import org.eyeseetea.malariacare.domain.boundary.repositories.IMediaRepository;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MediaRepository implements IMediaRepository {

    @Override
    public List<Media> getAll() {
        return MediaMapper.mapFromDbToDomain(getAllMediaDB());
    }

    public List<Media> getAllNotDownloaded() {
        return MediaMapper.mapFromDbToDomain(getAllNotInLocal());
    }

    public List<Media> getAllMediaByResourceUid(String url) {
        return MediaMapper.mapFromDbToDomain(getAllByResourceUid(url));
    }

    public List<Media> getAllDownloaded() {
        return MediaMapper.mapFromDbToDomain(getAllInLocal());
    }

    private List<MediaDB> getAllMediaDB() {
        return new Select().
                from(MediaDB.class).
                orderBy(MediaDB_Table.id_media, true).
                queryList();
    }

    private List<MediaDB> getAllNotInLocal() {
        return new Select().
                from(MediaDB.class).
                where(MediaDB_Table.filename.isNull()).
                and(MediaDB_Table.resource_url.isNotNull()).
                orderBy(MediaDB_Table.id_media, true).
                queryList();
    }

    private List<MediaDB> getAllInLocal() {
        return new Select().
                from(MediaDB.class).
                where(MediaDB_Table.filename.isNotNull()).
                and(MediaDB_Table.resource_url.isNotNull()).
                orderBy(MediaDB_Table.id_media, true).
                queryList();
    }


    /**
     * Returns a media that holds a reference to the same resource with an already downloaded copy
     * of the file.
     */
    public Media findByUid(String resourceUrl) {
        MediaDB mediaDB = new Select().from(MediaDB.class)
                .where(MediaDB_Table.filename.isNotNull())
                .and(MediaDB_Table.resource_url.is(resourceUrl))
                .querySingle();
        if (mediaDB == null) {
            return null;
        } else {
            return new Media(mediaDB.getId_media(),
                    Media.getFilenameFromPath(mediaDB.getFilename()), mediaDB.getFilename(),
                    mediaDB.getResourceUrl(), convertConstantToMediaType(mediaDB.getMediaType()),
                    MediaMapper.getSizeInMB(mediaDB.getFilename()));
        }
    }

    private List<MediaDB> getAllByResourceUid(String resourceUrl) {
        return new Select().from(MediaDB.class)
                .where(MediaDB_Table.resource_url.is(resourceUrl))
                .queryList();
    }

    /**
     * Returns a media that holds a reference to the same resource with an already downloaded copy
     * of the file.
     */
    public Media findLocalByUid(String resourceUrl) {
        MediaDB mediaDB = new Select().from(MediaDB.class)
                .where(MediaDB_Table.filename.isNotNull())
                .and(MediaDB_Table.resource_url.is(resourceUrl))
                .querySingle();
        if(mediaDB==null){
            return null;
        }else{
            return new Media(mediaDB.getId_media(), Media.getFilenameFromPath(mediaDB.getFilename()), mediaDB.getFilename(), mediaDB.getResourceUrl(), convertConstantToMediaType(mediaDB.getMediaType()),
                    MediaMapper.getSizeInMB(mediaDB.getFilename()));
        }
    }


    public Media.MediaType convertConstantToMediaType(int mediaType) {
        if (mediaType == Constants.MEDIA_TYPE_IMAGE) {
            return Media.MediaType.PICTURE;
        } else if (mediaType == Constants.MEDIA_TYPE_VIDEO) {
            return Media.MediaType.VIDEO;
        }
        return null;
    }

    public static int convertMediaTypeToConstant(Media.MediaType mediaType) {
        if (mediaType.equals(Media.MediaType.PICTURE)){
            return Constants.MEDIA_TYPE_IMAGE;
        } else if (mediaType.equals(Media.MediaType.VIDEO)){
                return Constants.MEDIA_TYPE_VIDEO;
        }
        return 0;
    }

    public void updateResourcePath(Media media) {
        MediaDB mediaDb = getMedia(media.getId());
        mediaDb.setFilename(media.getResourcePath());
        mediaDb.update();
    }

    private MediaDB getMedia(long id) {
        return new Select().
                from(MediaDB.class).
                where(MediaDB_Table.id_media.is(id))
                .querySingle();

    }

    public void updateModel(Media media) {
        MediaDB mediaDB = getMedia(media.getId());
        mediaDB.setFilename(media.getResourcePath());
        mediaDB.update();
    }

    public void deleteModel(Media media) {
        MediaDB mediaDb = getMedia(media.getId());
        mediaDb.delete();
    }

    public int updateSyncedFiles(HashMap<String, String> syncedFiles) {
        int correctSyncedFiles = 0;
        //Try to reuse a local copy if another media references same url
        if (syncedFiles.size() > 0) {
            for (String mediaUid : syncedFiles.keySet()) {
                String absolutePath = syncedFiles.get(mediaUid);
                List<Media> allMediaWithSameResource = getAllMediaByResourceUid(mediaUid);
                for (Media localMedia : allMediaWithSameResource) {
                    correctSyncedFiles++;
                    System.out.println(localMedia.toString() + "\tsaved in " + absolutePath);
                    localMedia.setResourcePath(absolutePath);
                    updateModel(localMedia);
                }
            }
        }
        return correctSyncedFiles;
    }

    public void updateNotDownloadedMedia(Media media) {
        MediaDB mediaDB = new Select().from(MediaDB.class).where(MediaDB_Table.resource_url.eq(media.getResourceUrl())).querySingle();
        if(mediaDB!=null){
            if(mediaDB.getFilename()!=null){
                System.out.println(mediaDB.getResourceUrl() +" is already downloaded");
                media=findLocalByUid(media.getResourceUrl());
            }
        }else{
            mediaDB = new MediaDB(convertMediaTypeToConstant(media.getType()), media.getResourceUrl());
            mediaDB.save();
        }
    }

    public static List<String> listOfDownloadedFiles(){
        List<String> paths = new ArrayList<>();
        List<MediaDB> mediaList = new Select().from(MediaDB.class).where(MediaDB_Table.filename.isNotNull()).queryList();
        for(MediaDB mediaDB:mediaList){
            paths.add(mediaDB.getFilename());
        }
        return paths;
    }
}
