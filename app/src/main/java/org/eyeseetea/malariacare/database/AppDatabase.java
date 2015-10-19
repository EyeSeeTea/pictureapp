package org.eyeseetea.malariacare.database;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by arrizabalaga on 19/10/15.
 */
@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION, foreignKeysSupported = true)
public class AppDatabase {
    public static final String NAME = "EyeSeeTeaDB";
    public static final int VERSION = 1;
}

