package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eyeseetea.malariacare.domain.exception.ActionNotAllowed;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;

public class InvalidLoginAttemptsTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void check_when_creates_login_enable() {
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(0, 0);
        assertThat(invalidLoginAttempts.isLoginEnabled(), is(true));
    }

    @Test
    public void check_not_disable_when_add_one_attempt() throws ActionNotAllowed {
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(0, 0);
        invalidLoginAttempts.addFailedAttempts();
        assertThat(invalidLoginAttempts.isLoginEnabled(), is(true));
    }

    @Test
    public void check_disable_after_add_three_attempts() throws ActionNotAllowed {
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(0, 0);
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();
        assertThat(invalidLoginAttempts.isLoginEnabled(), is(false));
    }

    @Test
    public void check_disable_after_add_three_attempts_and_twenty_seconds()
            throws InterruptedException, ActionNotAllowed {
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(0, 0);
        invalidLoginAttempts.setDisableTime(10);
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();

        Thread.sleep(5);
        assertThat(invalidLoginAttempts.isLoginEnabled(), is(false));
    }

    @Test
    public void check_enable_after_add_three_attempts_and_thirty_seconds()
            throws InterruptedException, ActionNotAllowed {
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(0, 0);
        invalidLoginAttempts.setDisableTime(10);
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();

        Thread.sleep(11);
        assertThat(invalidLoginAttempts.isLoginEnabled(), is(true));
    }


    @Test
    public void should_throw_exception_if_add_more_failed_attempts_that_allowed
            () throws InterruptedException, ActionNotAllowed {
        thrown.expect(ActionNotAllowed.class);
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(0, 0);
        invalidLoginAttempts.setDisableTime(10);
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();
    }

    @Test
    public void check_disable_if_in_constructor_passed_three_attempts() {
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(3,
                new Date().getTime() + 30000);
        assertThat(invalidLoginAttempts.isLoginEnabled(), is(false));
    }

    @Test
    public void check_enable_if_in_constructor_passed_time_enables() {
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(0,
                new Date().getTime() + 30000);
        assertThat(invalidLoginAttempts.isLoginEnabled(), is(false));
    }

    @Test
    public void check_enable_if_in_constructor_passed_two_attempts() throws ActionNotAllowed {
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(2, 0);
        invalidLoginAttempts.addFailedAttempts();
        assertThat(invalidLoginAttempts.isLoginEnabled(), is(false));
    }

}
