package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.eyeseetea.malariacare.data.database.model.MediaDB;
import org.eyeseetea.malariacare.data.repositories.MediaRepository;
import org.eyeseetea.malariacare.utils.Constants;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class MediaTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void throw_exception_if_name_not_provided_first_constructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name is required");

        new Media(null, "path", Media.MediaType.PICTURE, "30mb");
    }

    @Test
    public void throw_exception_if_path_not_provided_first_constructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("path is required");

        new Media("name", null, Media.MediaType.PICTURE, "30mb");
    }


    @Test
    public void throw_exception_if_type_not_provided_first_constructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("type is required");

        new Media("name", "path", null, "30mb");
    }

    @Test
    public void throw_exception_if_size_not_provided_first_constructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("size is required");

        new Media("name", "path", Media.MediaType.PICTURE, null);
    }

    @Test
    public void test_mediaDB_conversion_to_Media() {
        MediaDB mediaDB = new MediaDB(Constants.MEDIA_TYPE_IMAGE, "path", null);
        mediaDB.setFilename("name");
        Media media = MediaRepository.fromModel(mediaDB);
        assertThat(media.getName().equals("name"), is(true));
        assertThat(media.getPath().equals("path"), is(true));
        assertThat(media.getType() == Media.MediaType.PICTURE, is(true));
    }
}
