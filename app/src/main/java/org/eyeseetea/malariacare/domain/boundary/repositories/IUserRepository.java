package org.eyeseetea.malariacare.domain.boundary.repositories;

import org.eyeseetea.malariacare.domain.entity.UserAccount;

public interface IUserRepository {
    UserAccount getLoggedUser();

    void saveLoggedUser(UserAccount userAccount);
}
