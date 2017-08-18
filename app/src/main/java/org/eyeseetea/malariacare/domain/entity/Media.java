package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import org.eyeseetea.malariacare.R;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.sdk.common.FileUtils;

/**
 * Created by ignac on 04/08/2017.
 */

public class Media {

    public enum MediaType{ PICTURE, VIDEO};
    long id;
    String name;
    String resourcePath;
    String resourceUrl;
    MediaType type;
    String size;

    public Media(long id, String name, String resourcePath, String resourceUrl, MediaType type, String size) {
        this.id = required(id,"id is required");
        this.resourcePath = resourcePath;
        this.name = name;
        this.resourceUrl = required(resourceUrl,"resourceUrl is required");
        this.type = required(type,"type is required");
        this.size = required(size,"size is required");
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getSize() {
        return size;
    }

    public MediaType getType() {
        return type;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public Drawable getFileFromPath(Context context){
        //TODO
        return ContextCompat.getDrawable(context, R.drawable.preview);
    }

    public static String getFilenameFromPath(String filename) {
        if(filename==null) {
            return null;
        }else{
            return filename.substring(filename.lastIndexOf("/")+1, filename.length());
        }
    }

    public boolean isPicture() {
        return type == MediaType.PICTURE;
    }

    public boolean isVideo() {
        return type == MediaType.VIDEO;
    }
}
