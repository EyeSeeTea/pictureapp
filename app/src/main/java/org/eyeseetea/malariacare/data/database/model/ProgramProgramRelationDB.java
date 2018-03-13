package org.eyeseetea.malariacare.data.database.model;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Join;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

import java.util.List;

@Table(database = AppDatabase.class, name = "ProgramProgramRelation")
public class ProgramProgramRelationDB extends BaseModel {

    @Column
    @PrimaryKey(autoincrement = true)
    long id_program_program_relation;

    @Column
    long id_main_program;

    @Column
    long id_aux_program;

    public ProgramProgramRelationDB() {
    }

    public ProgramProgramRelationDB(long id_main_program,
            long id_aux_program) {
        this.id_main_program = id_main_program;
        this.id_aux_program = id_aux_program;
    }

    public long getId_program_program_relation() {
        return id_program_program_relation;
    }

    public long getId_main_program() {
        return id_main_program;
    }

    public long getId_aux_program() {
        return id_aux_program;
    }

    public static List<ProgramDB> getRelatedPrograms(long idProgram) {
        return new Select().from(ProgramDB.class)
                .as(AppDatabase.programName)
                .join(ProgramProgramRelationDB.class, Join.JoinType.INNER).as(
                        AppDatabase.programProgramRelationName)
                .on(ProgramDB_Table.id_program.withTable(AppDatabase.programAlias)
                        .eq(ProgramProgramRelationDB_Table.id_aux_program.withTable(
                                AppDatabase.programProgramRelationAlias)))
                .where(ProgramProgramRelationDB_Table.id_main_program.withTable(
                        AppDatabase.programProgramRelationAlias)
                        .is(idProgram))
                .queryList();
    }
}
