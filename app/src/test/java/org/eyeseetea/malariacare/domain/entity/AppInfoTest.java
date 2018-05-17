package org.eyeseetea.malariacare.domain.entity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AppInfoTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void should_throw_exception_if_medatadataversion_is_null() {
        thrown.expect(IllegalArgumentException.class);

        new AppInfo(null, "v1.3", "1.0");
    }

    @Test
    public void should_throw_exception_if_medatadataversion_is_empty() {
        thrown.expect(IllegalArgumentException.class);

        new AppInfo("", "v1.3", "1.0");
    }

    @Test
    public void should_throw_exception_if_appversion_is_null() {
        thrown.expect(IllegalArgumentException.class);

        new AppInfo("v1", null, "1.0");
    }

    @Test
    public void should_throw_exception_if_appversion_is_empty() {
        thrown.expect(IllegalArgumentException.class);

        new AppInfo("v1", "", null);
    }
    @Test
    public void should_throw_exception_if_medatadataversion_is_null_with_metadata_boolean() {
        thrown.expect(IllegalArgumentException.class);

        new AppInfo(null, "v1.3", "1.0");
    }
    @Test
    public void should_throw_exception_if_appversion_is_null_with_metadata_boolean() {
        thrown.expect(IllegalArgumentException.class);

        new AppInfo(true, null, "1.0");
    }

    @Test
    public void should_throw_exception_if_appversion_is_empty_with_metadata_boolean() {
        thrown.expect(IllegalArgumentException.class);

        new AppInfo(true, "", null);
    }
}
