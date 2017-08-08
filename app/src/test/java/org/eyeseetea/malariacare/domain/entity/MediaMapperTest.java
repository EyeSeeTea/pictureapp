package org.eyeseetea.malariacare.domain.entity;

import org.eyeseetea.malariacare.data.database.model.MediaDB;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

public class MediaMapperTest {
    public MediaMapperTest() {
    }

    @Test
    public void test_mediaDB_conversion_to_Media() {
        MediaDB mediaDB = new MediaDB(Constants.MEDIA_TYPE_IMAGE, "path", null);
        mediaDB.setFilename("name");
        Media media = MediaRepository.fromModel(mediaDB);
        Assert.assertThat(media.getName().equals("name"), Is.is(true));
        Assert.assertThat(media.getPath().equals("path"), Is.is(true));
        Assert.assertThat(media.getType() == Media.MediaType.PICTURE, Is.is(true));
    }
}