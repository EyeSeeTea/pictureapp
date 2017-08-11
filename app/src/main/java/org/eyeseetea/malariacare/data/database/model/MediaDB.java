/*
 * Copyright (c) 2015.
 *
 * This file is part of QA App.
 *
 *  Health Network QIS App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Health Network QIS App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.data.database.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.utils.Constants;

import java.util.ArrayList;
import java.util.List;

@Table(database = AppDatabase.class, name = "Media")
public class MediaDB extends BaseModel {

    /**
     * Null media value to express that a question has NO media without using queries
     */
    private static MediaDB noMedia = new MediaDB(Constants.NO_MEDIA_ID, null);

    @Column
    @PrimaryKey(autoincrement = true)
    long id_media;

    @Column
    int media_type;

    @Column
    String resource_url;

    @Column
    String filename;

    public MediaDB() {
    }

    public MediaDB(int media_type, String resource_url) {
        this.media_type = media_type;
        this.resource_url = resource_url;
        this.filename = null;
    }

    public long getId_media() {
        return id_media;
    }

    public int getMediaType() {
        return media_type;
    }

    public void setMediaType(int media_type) {
        this.media_type = media_type;
    }

    public String getResourceUrl() {
        return resource_url;
    }

    public void setResourceUrl(String resource_url) {
        this.resource_url = resource_url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Returns if is a picture
     */
    public boolean isPicture() {
        return (media_type == Constants.MEDIA_TYPE_IMAGE);
    }

    /**
     * Returns if is video
     */
    public boolean isVideo() {
        return (media_type == Constants.MEDIA_TYPE_VIDEO);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MediaDB media = (MediaDB) o;

        if (id_media != media.id_media) return false;
        if (media_type != media.media_type) return false;
        if (filename != media.filename) return false;
        return resource_url != null ? resource_url.equals(media.resource_url)
                : media.resource_url == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id_media ^ (id_media >>> 32));
        result = 31 * result + media_type;
        result = 31 * result + (resource_url != null ? resource_url.hashCode() : 0);
        result = 31 * result + (filename != null ? filename.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Media{" +
                "id_media=" + id_media +
                ", media_type=" + media_type +
                ", resource_url='" + resource_url + '\'' +
                ", filename=" + filename +
                '}';
    }
}
