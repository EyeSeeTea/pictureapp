ºpackage org.eyeseetea.malariacare.database.migrations;

        import android.database.sqlite.SQLiteDatabase;

        import com.raizlabs.android.dbflow.annotation.Migration;
        import com.raizlabs.android.dbflow.config.FlowManager;
        import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
        import com.raizlabs.android.dbflow.structure.ModelAdapter;

        import org.eyeseetea.malariacare.database.AppDatabase;
        import org.eyeseetea.malariacare.database.model.Question;
        import org.eyeseetea.malariacare.database.model.User;

/**
 * Created by manuel on 2/01/17.
 */
@Migration(version = 22, databaseName = AppDatabase.NAME)
public class Migration22TreatmentMatrix extends BaseMigration {

    public static final String ALTER_TABLE_ADD_COLUMN = "ALTER TABLE %s ADD COLUMN %s %s";
    private static String TAG = ".Migration22";

    public static void addColumn(SQLiteDatabase database, Class model, String columnName,
            String type) {
        ModelAdapter myAdapter = FlowManager.getModelAdapter(model);
        database.execSQL(
                String.format(ALTER_TABLE_ADD_COLUMN, myAdapter.getTableName(), columnName, type));
    }

    @Override
    public void migrate(SQLiteDatabase database) {
        addColumn(database, User.class, "organisation", "long");
        addColumn(database, User.class, "supervisor", "long");
    }

}
