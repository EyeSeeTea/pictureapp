package org.eyeseetea.malariacare.domain.entity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.model.MediaDB;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ignac on 04/08/2017.
 */

public class Media {
    String name;
    String path;
    int type;
    String size;

    public Media(String name, String path, int type, String size) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static Media fromModel(MediaDB mediaDB){
        return new Media(mediaDB.getFilename(), mediaDB.getResourceUrl(), mediaDB.getMediaType(),
                getSizeInMB(mediaDB.getFilename()));
    }

    private static String getSizeInMB(String filename) {
        //Todo fix this method using for example SizeCalculator.getSizeInMB(mediaDB.getFilename()) from eyeseetesdk
        return "0mb";
    }

    public static List<Media> fromModel(List<MediaDB> mediaDBs){
        List<Media> medias = new ArrayList<>();
        for(MediaDB mediaDB:mediaDBs){
            medias.add(fromModel(mediaDB));
        }
        return medias;
    }

    public static String getFileSize(){
        //TODO
        return "0MB";
    }

    public Drawable getFileFromPath(Context context){
        //TODO
        return ContextCompat.getDrawable(context, R.drawable.preview);
    }

    public boolean isPicture() {
        return type == Constants.MEDIA_TYPE_IMAGE;
    }

    public boolean isVideo() {
        return type == Constants.MEDIA_TYPE_VIDEO;
    }
}
