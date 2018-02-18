package org.eyeseetea.malariacare;


import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.EyeSeeTeaGeneratedDatabaseHolder;

public class DatabaseHolderProviderStrategy implements
        EyeSeeTeaApplication.IDatabaseHolderProviderStrategy {
    @Override
    public Class<? extends DatabaseHolder> provide() {
        return EyeSeeTeaGeneratedDatabaseHolder.class;
    }
}
