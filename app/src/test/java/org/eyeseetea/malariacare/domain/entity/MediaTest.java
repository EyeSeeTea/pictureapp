package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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

}
