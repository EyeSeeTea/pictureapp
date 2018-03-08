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


}
