package org.eyeseetea.malariacare.data.database.datasources.strategies;

import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesState;
import org.eyeseetea.malariacare.domain.entity.Program;
import org.eyeseetea.malariacare.domain.entity.Survey;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.domain.entity.UserAccount;

public class SurveyLocalDataSourceStrategy extends ASurveyLocalDataSourceStrategy {
    @Override
    public Survey createNewSurvey() {
        ProgramDB programDB = ProgramDB.findById(PreferencesEReferral.getUserProgramId());
        UserDB userDB = UserDB.getLoggedUser();
        if (programDB == null || userDB == null) {
            return null;
        }
        UserAccount userAccount = new UserAccount(userDB.getName(),
                userDB.getUid(),
                PreferencesState.getCredentialsFromPreferences().isDemoCredentials());
        if (programDB == null) {
            return null;
        }
        Program program = new Program(programDB.getUid(), programDB.getUid());
        return Survey.createNewSurvey(program, userAccount);
    }
}
