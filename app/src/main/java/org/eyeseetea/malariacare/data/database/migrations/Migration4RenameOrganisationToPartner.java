package org.eyeseetea.malariacare.data.database.migrations;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.eyeseetea.malariacare.data.database.AppDatabase;
import org.eyeseetea.malariacare.data.database.utils.populatedb.FileCsvs;
import org.eyeseetea.malariacare.data.database.utils.populatedb.PopulateDB;

import java.io.IOException;

@Migration(version = 4, database = AppDatabase.class)
public class Migration4RenameOrganisationToPartner extends BaseMigration {
    private static String TAG = ".Migration4";

    Migration4RenameOrganisationToPartner instance;


    public Migration4RenameOrganisationToPartner() {
        instance = this;
    }

    @Override
    public void migrate(DatabaseWrapper database) {

        String sqlCopy = "INSERT INTO Partner (id_partner, uid_partner, name)"
                + " SELECT id_organisation, uid_organisation, name"
                + " FROM Organisation;";
        database.execSQL(sqlCopy);
        String sqlDelete = "DROP TABLE IF EXISTS Organisation;";
        database.execSQL(sqlDelete);

        String sqlUserTemp =
                "CREATE TABLE UserTemp(id_user INTEGER PRIMARY KEY, uid_user TEXT, name TEXT, "
                        + "partner_fk INTEGER, supervisor_fk INTEGER)";
        database.execSQL(sqlUserTemp);
        String sqlCopyUser =
                "INSERT INTO UserTemp (id_user, uid_user, name, partner_fk, supervisor_fk)"
                        + " SELECT id_user, uid_user, name ,organisation_fk ,supervisor_fk"
                        + " FROM User;";
        database.execSQL(sqlCopyUser);
        String sqlDeleteUser = "DROP TABLE IF EXISTS User;";
        database.execSQL(sqlDeleteUser);
        String renameUser = "ALTER TABLE UserTemp RENAME TO User";
        database.execSQL(renameUser);

        String sqlTreatmentTemp =
                "CREATE TABLE TreatmentTemp(id_treatment INTEGER PRIMARY KEY, "
                        + "id_partner_fk INTEGER, diagnosis INTEGER, message INTEGER, type "
                        + "INTEGER)";
        database.execSQL(sqlTreatmentTemp);
        String sqlCopyTreatment =
                "INSERT INTO TreatmentTemp (id_treatment, id_partner_fk, diagnosis, message, type)"
                        + " SELECT id_treatment, id_organisation_fk, diagnosis, message, type"
                        + " FROM Treatment;";
        database.execSQL(sqlCopyTreatment);
        String sqlDeleteTreatment = "DROP TABLE IF EXISTS Treatment;";
        database.execSQL(sqlDeleteTreatment);
        String renameTreatment = "ALTER TABLE TreatmentTemp RENAME TO Treatment";
        database.execSQL(renameTreatment);

        FileCsvs fileCsvs = new FileCsvs();
        try {
            fileCsvs.copyCsvFile("Organisations.csv", PopulateDB.PARTNER_CSV);
        } catch (IOException e) {
            Log.e(TAG, "Error copying csv\n" + e.getMessage());
            e.printStackTrace();
        }

//        TreatmentTable treatmentTable=new TreatmentTable();
//        try {
//            treatmentTable.generateTreatmentMatrix();
//        } catch (IOException e) {
//            Log.e(TAG, "Error getting treatment\n" + e.getMessage());
//            e.printStackTrace();
//        }
    }
}
