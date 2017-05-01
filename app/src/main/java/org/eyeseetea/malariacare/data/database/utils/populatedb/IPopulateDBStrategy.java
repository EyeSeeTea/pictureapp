package org.eyeseetea.malariacare.data.database.utils.populatedb;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface IPopulateDBStrategy {
    void init();

    InputStream openFile(Context context, String table)
            throws IOException, FileNotFoundException;
    void logoutWipe();
    void createDummyOrganisationInDB();
    void createDummyOrgUnitsDataInDB(Context context);
}
