package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class MediaTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void should_get_null_name_and_localPath_if_not_provided_in_complete_media_contructor() {

        Media media = new Media(1, null, null, "remotePath", Media.MediaType.PICTURE, "30mb", "programUid");
        assertTrue(media.getName()==null);
        assertTrue(media.getResourcePath()==null);
    }

    @Test
    public void should_get_null_if_remote_path_not_provided_in_complete_media_contructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("resourceUrl is required");

        new Media(1, "name", "localPath", null, Media.MediaType.PICTURE, "30mb", "programUid");

    }
    @Test
    public void should_throw_exception_if_type_not_provided_in_complete_media_constructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("type is required");

        new Media(1, "name", "localPath", "localPath", null, "30mb", "programUid");

    }

    @Test
    public void should_throw_exception_if_size_not_provided_in_complete_media_constructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("size is required");

        new Media(1, "name", "resourcePath", "localPath", Media.MediaType.PICTURE, null, "programUid");
    }

    @Test
    public void should_throw_exception_if_program_not_provided_in_complete_media_constructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("program is required");

        new Media(1, "name", "resourcePath", "localPath", Media.MediaType.PICTURE, "30mn", null);
    }

    @Test
    public void should_throw_exception_if_remote_path_not_provided_in_simple_media_constructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("resourceUrl is required");

        new Media(null, "localPath", Media.MediaType.PICTURE, "programUid");
    }

    @Test
    public void should_get_null_if__resourcePath_not_provided_in_simple_media_constructor() {
        Media media = new Media("resourceUrl", null, Media.MediaType.PICTURE, "programUid");
        assertTrue(media.getResourcePath()==null);
    }

    @Test
    public void throw_exception_if_type_not_provided_in_simple_media_constructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("type is required");

        new Media("resourceUrl", "localPath", null, "programUid");
    }

    @Test
    public void throw_exception_if_program_not_provided_in_simple_media_constructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("program is required");

        new Media("resourceUrl", "localPath", Media.MediaType.PICTURE, null);
    }

}
