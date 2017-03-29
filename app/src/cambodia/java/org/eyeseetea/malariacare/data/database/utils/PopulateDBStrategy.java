package org.eyeseetea.malariacare.data.database.utils;

import android.content.Context;
import android.content.res.AssetManager;

import org.eyeseetea.malariacare.data.database.utils.populatedb.IPopulateDBStrategy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PopulateDBStrategy implements IPopulateDBStrategy {

    public void init() {
    }

    public InputStream openFile(Context context, String table) throws IOException,
            FileNotFoundException {
        AssetManager assetMgr = context.getAssets();
        return assetMgr.open(table);
    }

    public static void createDummyOrganisationInDB() {
    }
}
