package org.eyeseetea.malariacare.domain.entity;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class UIDGeneratorTest {
    private static long FIRST_UID = 1000000000l;
    private static long STARTING_DATE_DECISECONDS = 14832252000l;

    @Test
    public void uid_generated_greater_than_first_uid() {
        UIDGenerator uidGenerator = new UIDGenerator();
        long uid = uidGenerator.generateUID();
        assertThat(true, is(uid > FIRST_UID));
    }

    @Test
    public void deciseconds_from_uid_lower_or_equal_than_deciseconds_from_after() {
        UIDGenerator uidGenerator = new UIDGenerator();
        long uid = uidGenerator.generateUID();
        long time_deciseconds = System.currentTimeMillis() / 100;
        long uidToDeciseconds = uid + STARTING_DATE_DECISECONDS - FIRST_UID;
        assertThat(true, is(time_deciseconds >= uidToDeciseconds));
    }

    @Test
    public void deciseconds_from_uid_greater_or_equal_than_deciseconds_from_before() {
        long time_deciseconds = System.currentTimeMillis() / 100;
        UIDGenerator uidGenerator = new UIDGenerator();
        long uid = uidGenerator.generateUID();
        long uidToDeciseconds = uid + STARTING_DATE_DECISECONDS - FIRST_UID;
        assertThat(true, is(time_deciseconds <= uidToDeciseconds));
    }

}
