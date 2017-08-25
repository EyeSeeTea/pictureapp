package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.database.model.UserDB;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.UserAccount;


public class UserAccountDataSource implements IUserRepository {
    @Override
    public UserAccount getLoggedUser() {
        UserDB userDB = UserDB.getLoggedUser();
        UserAccount userAccount = null;
        if (userDB != null) {
            userAccount = new UserAccount(userDB.getName(), userDB.getUid(), false);
            userAccount.setCanAddSurveys(userDB.canAddSurveys());
        }

        return userAccount;
    }

    @Override
    public void saveLoggedUser(UserAccount userAccount) {
        UserDB userDB = new UserDB(userAccount.getUserUid(), userAccount.getUserName());
        userDB.setCanAddSurveys(userAccount.canAddSurveys());
        UserDB.insertLoggedUser(userDB);
    }
}
