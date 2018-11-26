package org.eyeseetea.malariacare.domain.entity;

import static org.eyeseetea.malariacare.domain.utils.RequiredChecker.required;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import org.eyeseetea.malariacare.R;

import java.io.File;

public class Media {

    public enum MediaType {PICTURE, VIDEO, UNKNOWN}

    ;
    long id;
    String name;
    String resourcePath;
    String resourceUrl;
    String program;
    MediaType type;
    String size;

    public Media(String resourceUrl, String resourcePath, MediaType type, String program) {
        this.resourceUrl = required(resourceUrl,"resourceUrl is required");
        this.resourcePath = resourcePath;
        this.type = required(type,"type is required");
        this.program = required(program, "program is required");
    }

    public Media(long id, String name, String resourcePath, String resourceUrl, MediaType type, String size, String program) {
        this.id = id;
        this.resourcePath = resourcePath;
        this.name = name;
        this.resourceUrl = required(resourceUrl,"resourceUrl is required");
        this.type = required(type,"type is required");
        this.size = required(size,"size is required");
        this.program = required(program, "program is required");
    }

    public String getProgram() {
        return program;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getResourceUrl() {
        return resourceUrl;
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
        if (type.equals(MediaType.PICTURE)) {
            File file = new File(getResourcePath());
            Uri uri = Uri.fromFile(file);
        }else{

        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Media media = (Media) o;

        if (id != media.id) return false;
        if (name != null ? !name.equals(media.name) : media.name != null) return false;
        if (resourcePath != null ? !resourcePath.equals(media.resourcePath)
                : media.resourcePath != null) {
            return false;
        }
        if (resourceUrl != null ? !resourceUrl.equals(media.resourceUrl)
                : media.resourceUrl != null) {
            return false;
        }
        if (program != null ? !program.equals(media.program) : media.program != null) return false;
        if (type != media.type) return false;
        return size != null ? size.equals(media.size) : media.size == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (resourcePath != null ? resourcePath.hashCode() : 0);
        result = 31 * result + (resourceUrl != null ? resourceUrl.hashCode() : 0);
        result = 31 * result + (program != null ? program.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", resourcePath='" + resourcePath + '\'' +
                ", resourceUrl='" + resourceUrl + '\'' +
                ", program='" + program + '\'' +
                ", type=" + type +
                ", size='" + size + '\'' +
                '}';
    }
}
