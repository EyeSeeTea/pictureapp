package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import java.util.Date;

public class InvalidLoginAttemptsTest {

    @Test
    public void check_when_creates_login_enable() {
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(0, 0);
        assertThat(invalidLoginAttempts.isLoginEnabled(), is(true));
    }

    @Test
    public void check_not_disable_when_add_one_attempt() {
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(0, 0);
        invalidLoginAttempts.addFailedAttempts();
        assertThat(invalidLoginAttempts.isLoginEnabled(), is(true));
    }

    @Test
    public void check_disable_after_add_three_attempts() {
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(0, 0);
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();
        assertThat(invalidLoginAttempts.isLoginEnabled(), is(false));
    }

    @Test
    public void check_disable_after_add_three_attempts_and_twenty_seconds()
            throws InterruptedException {
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(0, 0);
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();


        Thread.sleep(20000);
        assertThat(invalidLoginAttempts.isLoginEnabled(), is(false));
    }

    @Test
    public void check_enable_after_add_three_attempts_and_thirty_seconds()
            throws InterruptedException {
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(0, 0);
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();

        Thread.sleep(30001);
        assertThat(invalidLoginAttempts.isLoginEnabled(), is(true));
    }


    @Test
    public void check_enable_after_add_five_attempts_and_thirty_seconds_and_add_one_attempt_after
            () throws InterruptedException {
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(0, 0);
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();
        invalidLoginAttempts.addFailedAttempts();

        Thread.sleep(30001);
        invalidLoginAttempts.addFailedAttempts();
        assertThat(invalidLoginAttempts.isLoginEnabled(), is(true));

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
    public void check_enable_if_in_constructor_passed_two_attempts() {
        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(2, 0);
        invalidLoginAttempts.addFailedAttempts();
        assertThat(invalidLoginAttempts.isLoginEnabled(), is(false));
    }

}
