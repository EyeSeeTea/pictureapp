package org.eyeseetea.malariacare.data.database;

import org.eyeseetea.malariacare.data.database.utils.PreferencesEReferral;
import org.eyeseetea.malariacare.domain.boundary.repositories.IInvalidLoginAttemptsRepository;
import org.eyeseetea.malariacare.domain.entity.InvalidLoginAttempts;


public class InvalidLoginAttemptsRepositoryLocalDataSource implements
        IInvalidLoginAttemptsRepository {
    @Override
    public InvalidLoginAttempts getInvalidLoginAttempts() {
        int badLoginAttempts = PreferencesEReferral.getNumberBadLogin();
        long enableLoginTime = PreferencesEReferral.getTimeLoginEnables();

        InvalidLoginAttempts invalidLoginAttempts = new InvalidLoginAttempts(badLoginAttempts,
                enableLoginTime);

        return invalidLoginAttempts;
    }

    @Override
    public void saveInvalidLoginAttempts(InvalidLoginAttempts invalidLoginAttempts) {
        PreferencesEReferral.setTimeLoginEnables(invalidLoginAttempts.getEnableLoginTime());
        PreferencesEReferral.setBadLogin(invalidLoginAttempts.getFailedLoginAttempts());
    }
}
