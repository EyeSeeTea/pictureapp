package org.eyeseetea.malariacare.domain.entity;

import java.util.Date;

public class UIDGenerator {

    private static long FIRST_UID = 1000000000l;
    private static long STARTING_DATE_DECISECONDS = 14832252000l;

    public static long generateUID() {
        long decisecond_now_time = new Date().getTime() / 100;
        return decisecond_now_time - STARTING_DATE_DECISECONDS + FIRST_UID;
    }


}
