package org.eyeseetea.malariacare.data.database.model;


import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.eyeseetea.malariacare.data.database.AppDatabase;

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
}
