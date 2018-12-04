package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

public class AppInfoTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void should_throw_exception_if_medatadataversion_is_null() {
        thrown.expect(IllegalArgumentException.class);

        new AppInfo(null, "v1.3", "1.0", null,null);
    }

    @Test
    public void should_throw_exception_if_medatadataversion_is_empty() {
        thrown.expect(IllegalArgumentException.class);

        new AppInfo("", "v1.3", "1.0", null,null);
    }

    @Test
    public void should_throw_exception_if_appversion_is_null() {
        thrown.expect(IllegalArgumentException.class);

        new AppInfo("v1", null, "1.0", null,null);
    }

    @Test
    public void should_throw_exception_if_appversion_is_empty() {
        thrown.expect(IllegalArgumentException.class);

        new AppInfo("v1", "", null,null,null);
    }
    @Test
    public void should_throw_exception_if_medatadataversion_is_null_with_metadata_boolean() {
        thrown.expect(IllegalArgumentException.class);

        new AppInfo(null, "v1.3", "1.0", null,null);
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

    @Test
    public void should_return_last_push_date_on_getLastPushDateMethod(){
        Date date=new Date();
        AppInfo appInfo=new AppInfo("v1", "v1.3", "1.0", null,date);
        assertThat(appInfo.getLastPushDate(), is(date));
    }
}
