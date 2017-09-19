package org.eyeseetea.malariacare.domain.entity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DeviceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();  

    @Test
    public void should_throw_exception_if_androidversion_is_null() {
        thrown.expect(IllegalArgumentException.class);

        new Device("6568333832", "kfndmnfdmf", null);
    }

    @Test
    public void should_throw_exception_if_androidversion_is_empty() {
        thrown.expect(IllegalArgumentException.class);

        new Device("6568333832", "kfndmnfdmf", "");
    }
}
