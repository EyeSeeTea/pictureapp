package org.eyeseetea.malariacare.data.database.datasources;

import com.raizlabs.android.dbflow.sql.language.Select;

import org.eyeseetea.malariacare.data.database.model.PhoneFormatDB;
import org.eyeseetea.malariacare.data.database.model.PhoneFormatDB_Table;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.domain.boundary.repositories.IPhoneFormatRepository;
import org.eyeseetea.malariacare.domain.entity.PhoneFormat;


public class PhoneFormatLocalDataSource implements IPhoneFormatRepository {
    @Override
    public PhoneFormat getUserPhoneFormat() {
        PhoneFormatDB phoneFormatDB = getPhoneFormatByProgram(
                PreferencesEReferral.getUserProgramId());
        return new PhoneFormat(phoneFormatDB.getPhoneMask(), phoneFormatDB.getTrunkPrefix(),
                phoneFormatDB.getPrefixToPut());
    }

    private PhoneFormatDB getPhoneFormatByProgram(Long programId) {
        return new Select()
                .from(PhoneFormatDB.class)
                .where(PhoneFormatDB_Table.id_program_fk.eq(programId))
                .querySingle();
    }
}
