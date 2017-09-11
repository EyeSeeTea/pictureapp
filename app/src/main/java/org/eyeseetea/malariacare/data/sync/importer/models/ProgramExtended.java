/*
 * Copyright (c) 2015.
 *
 * This file is part of QA App.
 *
 *  Health Network QIS App is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Health Network QIS App is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.eyeseetea.malariacare.data.sync.importer.models;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.data.database.model.Program;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.AttributeFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.AttributeFlow_Table;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramFlow;
import org.hisp.dhis.client.sdk.android.api.persistence.flow.ProgramFlow_Table;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arrizabalaga on 5/11/15.
 */
public class ProgramExtended {

    private static final String TAG = ".PRExtended";

    /**
     * Hardcoded 'code' of the attribute that holds the idx of productiviy in the orgunit array
     * attribute
     */
    public static final String PROGRAM_PRODUCTIVITY_POSITION_ATTRIBUTE_CODE = "PPP";

    /**
     * Reference to sdk program
     */
    ProgramFlow program;

    /**
     * Reference to app program (useful to create relationships with orgunits)
     */
    Program appProgram;

    public ProgramExtended() {
    }

    public ProgramExtended(ProgramFlow program) {
        this.program = program;
    }

    public ProgramFlow getProgram() {
        return this.program;
    }

    public void setAppProgram(Program appProgram) {
        this.appProgram = appProgram;
    }

    public Program getAppProgram() {
        return this.appProgram;
    }

    public static ProgramFlow getProgram(String id) {
        return new Select()
                .from(ProgramFlow.class).where(ProgramFlow_Table.uId.eq(id)).querySingle();
    }


    public String getUid() {
        return program.getUId();
    }

    public String getDisplayName() {
        return program.getDisplayName();
    }

    public String getName() {
        return program.getName();
    }

    public static List<ProgramExtended> getExtendedList(List<ProgramFlow> flowList) {
        List<ProgramExtended> extendedsList = new ArrayList<>();
        for (ProgramFlow flowPojo : flowList) {
            extendedsList.add(new ProgramExtended(flowPojo));
        }
        return extendedsList;
    }
}
