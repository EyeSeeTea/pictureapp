package org.eyeseetea.malariacare.data.database.datasources.strategies;

import org.eyeseetea.malariacare.data.database.model.ProgramDB;
import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.domain.entity.Survey;

public class SurveyLocalDataSourceStrategy extends ASurveyLocalDataSourceStrategy {
    @Override
    public Survey createNewSurvey() {
        ProgramDB programDB = ProgramDB.findById(PreferencesEReferral.getUserProgramId());
        UserDB userDB = UserDB.getLoggedUser();

        if (programDB == null || userDB == null || programDB == null) {
            return null;
        }

        return Survey.createNewSurvey(programDB.getUid(), userDB.getUid());
    }
}
