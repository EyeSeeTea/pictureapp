package org.eyeseetea.malariacare.data.database.utils;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.hisp.dhis.client.sdk.android.api.persistence.DbDhis;

import java.util.Arrays;
import java.util.List;

public class ExportDataStrategy extends AExportDataStrategy {
    @Override
    public List<String> getDatabaseNames() {
        return Arrays.asList(AppDatabase.NAME + ".db", DbDhis.NAME + ".db");
    }
}
