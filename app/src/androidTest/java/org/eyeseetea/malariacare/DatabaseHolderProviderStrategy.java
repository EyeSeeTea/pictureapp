package org.eyeseetea.malariacare;


import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.EyeSeeTeaInMemoryGeneratedDatabaseHolder;

public class DatabaseHolderProviderStrategy implements
        EyeSeeTeaApplication.IDatabaseHolderProviderStrategy {
    @Override
    public Class<? extends DatabaseHolder> provide() {
        return EyeSeeTeaInMemoryGeneratedDatabaseHolder.class;
    }
}
