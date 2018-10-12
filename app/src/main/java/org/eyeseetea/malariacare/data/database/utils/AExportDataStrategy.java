package org.eyeseetea.malariacare.data.database.utils;

import android.support.annotation.NonNull;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.Arrays;
import java.util.List;

public class AExportDataStrategy {

    @NonNull
    public List<String> getDatabaseNames() {
        return Arrays.asList(AppDatabase.NAME + ".db");
    }
}
