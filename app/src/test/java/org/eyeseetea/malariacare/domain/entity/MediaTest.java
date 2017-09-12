package org.eyeseetea.malariacare.domain.entity;

import org.junit.Rule;
import org.junit.rules.ExpectedException;


public class MediaTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

/*    @Test
    public void throw_exception_if_remote_path_not_provided_first_constructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("resourceUrl is required");

        new Media(1, "name", "localPath", null, Media.MediaType.PICTURE, "30mb");
    }


    @Test
    public void throw_exception_if_type_not_provided_first_constructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("type is required");

        new Media(1, "name", "localPath", "externalPath", null, "30mb");
    }

    @Test
    public void throw_exception_if_size_not_provided_first_constructor() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("size is required");

        new Media(1, "name", "localPath", "externalPath", Media.MediaType.PICTURE, null);
    }*/

}
