package org.eyeseetea.malariacare.data.database.datasources;

import org.eyeseetea.malariacare.data.database.model.User;
import org.eyeseetea.malariacare.domain.boundary.repositories.IUserRepository;
import org.eyeseetea.malariacare.domain.entity.UserAccount;


public class UserAccountDataSource implements IUserRepository {
    @Override
    public UserAccount getLoggedUser() {
        User user = User.getLoggedUser();
        UserAccount userAccount = null;
        if (user != null) {
            userAccount = new UserAccount(user.getName(), user.getUid(), false);
        }
        return userAccount;
    }

    @Override
    public void saveLoggedUser(UserAccount userAccount) {
        User user = new User(userAccount.getUserUid(), userAccount.getUserName());
        User.insertLoggedUser(user);
    }
}
