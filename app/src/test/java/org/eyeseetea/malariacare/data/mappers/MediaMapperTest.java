package org.eyeseetea.malariacare.data.mappers;

import org.eyeseetea.malariacare.data.database.model.MediaDB;
import org.eyeseetea.malariacare.domain.entity.Media;
import org.eyeseetea.malariacare.utils.Constants;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

public class MediaMapperTest {
    public MediaMapperTest() {
    }

    @Test
    public void test_mediaDB_conversion_to_Media() {
        MediaDB mediaDB = new MediaDB(Constants.MEDIA_TYPE_IMAGE, "path");
        mediaDB.setFilename("name");
        Media media = MediaMapper.mapFromDbToDomain(mediaDB);
        Assert.assertThat(media.getName().equals("name"), Is.is(true));
        Assert.assertThat(media.getResourceUrl().equals("path"), Is.is(true));
        Assert.assertThat(media.getType() == Media.MediaType.PICTURE, Is.is(true));
    }
}