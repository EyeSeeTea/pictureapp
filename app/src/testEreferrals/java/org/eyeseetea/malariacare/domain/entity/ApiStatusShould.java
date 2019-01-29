package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

public class ApiStatusShould {

    @Test
    public void return_available_if_status_available() {
        ApiStatus apiStatus = new ApiStatus(true, "");
        assertThat(apiStatus.isAvailable(), is(true));
    }

    @Test
    public void return_no_available_if_status_is_no_available() {
        ApiStatus apiStatus = new ApiStatus(false, "");
        assertThat(apiStatus.isAvailable(), is(false));
    }

    @Test
    public void return_correct_message() {
        String message = "Test message";
        ApiStatus apiStatus = new ApiStatus(false, message);
        assertThat(apiStatus.getMessage(), is(message));
    }

}
