package org.eyeseetea.malariacare.data.database.utils;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.Model;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;

public class DatabaseUtils {
    public static void saveBatch(final List<Model> insertModels) {
//Save questions in batch

        DatabaseDefinition databaseDefinition =
                FlowManager.getDatabase(AppDatabase.class);
        databaseDefinition.executeTransaction(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                for (Model model : insertModels) {
                    model.insert();
                }
            }
        });
    }

    public static void deleteBatch(final List<Model> insertModels) {
//Save questions in batch

        DatabaseDefinition databaseDefinition =
                FlowManager.getDatabase(AppDatabase.class);
        databaseDefinition.executeTransaction(new ITransaction() {
            @Override
            public void execute(DatabaseWrapper databaseWrapper) {
                for (Model model : insertModels) {
                    model.delete();
                }
            }
        });
    }
}
